package de.henrik.algorithm;

import de.henrik.data.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static de.henrik.Main.graph;
import static de.henrik.Main.provider;
import static de.henrik.algorithm.Util.*;


public class TTCGroups extends Algorithm {

    private ConcurrentApplicationHashMap applicationHashMap;
    HighestPriorityAlgorithm highestPriorityAlgorithm;

    public TTCGroups(long seed) {
        super(seed);
    }

    @Override
    @SuppressWarnings("Duplicates")
    void startAlgorithm() {
        //do a highest priority first approach
        System.out.println("Starting Highest Priority Algorithm for initial assignment");
        highestPriorityAlgorithm = new HighestPriorityAlgorithm(seed);
        highestPriorityAlgorithm.startAlgorithm();
        highestPriorityAlgorithm = null;
        applicationHashMap = provider.applicationsProvider.getConcurrentApplicationHashMap();


        System.out.println("Starting TTC Only Algorithm");
        ConcurrentApplicationHashMap applicationHashMap = provider.applicationsProvider.getConcurrentApplicationHashMap();

        var groups = provider.studentAndGroupProvider.getGroupsBySize().get(1);
        boolean improvementMade;
        int iteration = 0;
        double allKeys = groups.stream().mapToInt(Group::getCollectionSize).sum();

        do {
            System.out.println("Iteration: " + ++iteration);
            improvementMade = false;
            int counter = 0;
            for (Group group : groups) {
                for (int collectionID = 1; collectionID <= group.getCollectionSize(); collectionID++) {
                    System.out.print("Progress " + String.format("%.2f", ((counter++ + collectionID - 1) / allKeys) * 100) + "%           \r");
                    //Paint Group
                    if (slow) highlightElement(graph.getNode(group.toString()));
                    checkPause();

                    int currentPriority = group.getPriority(collectionID);
                    if (currentPriority == 1) {
                        continue;
                    }
                    if (verbose)
                        System.out.println("\nTrying to find a better application for" + group + " with current " + group.getAcceptedApplication(collectionID));
                    for (Application application : group.getApplicationsFromCollection(collectionID)) {
                        if (application.priority() == currentPriority) {
                            if (verbose)
                                System.out.println("Did not find a better application for " + group + " in collection " + collectionID);
                            break;
                        }

                        // PaintApplication we are looking at
                        if (verbose) System.out.println("Testing application " + application);
                        if (slow) highlightElement(graph.getEdge(application.name()));
                        checkPause();

                        Topic topic = application.topic();

                        if (singleAssignment(application) || multiAssignment(application)) {
                            applicationHashMap.removeAllWithSameKey(application);
                            improvementMade = true;
                            if (verbose)
                                System.out.println("Found better assignment for " + group + " in collection " + collectionID);
                            break;
                        }

                        // Now we have to do stuff with swapping.
                        // We filter all current applications of this topic for with which one we can swap to still get a valid assignment.
                        // Then we iterate over these by size to gives us the best possible fill (size wise) of the slot.
                        var otherApplications = topic.acceptedApplications().stream().filter(application1 -> {
                            var slot = topic.getSlotOfApplication(application1);
                            return slot.participants() - application1.size() + application.size() >= topic.slotSize().first() && slot.participants() - application1.size() + application.size() <= topic.slotSize().second();
                        }).sorted(Comparator.comparingInt(Application::size)).toList();
                        for (var otherApplication : otherApplications)
                            if (swapGroups(application, otherApplication.getGroupAndCollectionKey(), 0, currentPriority == -1 ? 1000 : currentPriority, application, new HashSet<>())) {
                                improvementMade = true;
                                if (currentPriority != -1)
                                    group.getApplicationsFromCollection(collectionID).get(currentPriority - 1).removeApplication();
                                else {
                                    applicationHashMap.removeAllWithSameKey(application);
                                    applicationHashMap.removeAllWithSameKey(otherApplication);
                                }
                                break;
                            }
                    }
                    if (slow) Util.repaintGraph();
                    checkPause();
                }
                if (slow) Util.repaintGraph();
            }
            if (slow) Util.repaintGraph();
        } while (improvementMade);
        Util.repaintGraph();
    }

    private boolean multiAssignment(Application application) {
        // Check if we can find enough unassigned applications that fit in this slot together with the current application
        // its a bit of a waste to compute this every time, but i want to do it step by step first
        var topic = application.topic();
        var possibleApplications = applicationHashMap.getByTopic(topic);
        possibleApplications.remove(application);
        boolean improvementMade = false;
        for (var slotID : topic.possibleSlots(application)) {
            var currentParticipants = topic.slots().get(slotID).participants();
            var possibleApplicationsForSlot = multiObjectiveKnapsack(possibleApplications, topic.slotSize().second() - currentParticipants - application.size());
            if (possibleApplicationsForSlot.size() > 0) {
                application.topic().acceptApplication(application, slotID);
                applicationHashMap.removeAllWithSameKey(application);
                for (var app : possibleApplicationsForSlot) {
                    applicationHashMap.removeAllWithSameKey(app);
                    application.topic().acceptApplication(app, slotID);
                }
                improvementMade = true;
                break;
            }
        }
        return improvementMade;
    }

    private boolean singleAssignment(Application application) {
        var topic = application.topic();
        // Check if we can assign this, because there is free space and we can fill minSize (i dont think this will ever happen)
        for (var slotID : topic.possibleSlots(application)) {
            var currentParticipants = topic.slots().get(slotID).participants();
            if (currentParticipants + application.size() <= topic.slotSize().second() && currentParticipants + application.size() >= topic.slotSize().first()) {
                application.topic().acceptApplication(application, slotID);
                applicationHashMap.removeAllWithSameKey(application);
                return true;
            }
        }
        return false;
    }


    /**
     * Recursively swap groups to minimize overall priority.
     *
     * @param application     the application to swap
     * @param currentPriority the current priority level
     * @param currentGroupKey the group we want to swap with and the collectionID
     * @param processedGroups a set of processed groups to avoid infinite recursion
     * @return true if a valid swap is found and performed, false otherwise
     */
    @SuppressWarnings("Duplicates")
    private boolean swapGroups(Application application, Tupel<Group, Integer> currentGroupKey, int currentPriority, int maxPriority, Application initialApplication, Set<Tupel<Group, Integer>> processedGroups) {
        // TODO: 17.07.2023 i think we can vastly optimize this if we save the groups that cannot improve in this iteration, and dont look at them again. if we make any changes we delete the affected groups from this set.

        if (verbose)
            System.out.println("Swap called with application" + application + " currentGroup: " + currentGroupKey.toString() + " currentPriority: " + currentPriority + " maxPriority: " + maxPriority + " groupsLookedAt" + processedGroups.size());

        var currentGroup = currentGroupKey.first();
        var collectionID = currentGroupKey.second();
        var currentGroupAcceptedApplication = currentGroup.getAcceptedApplication(collectionID);

        if (slow) {
            highlightElement(graph.getNode(currentGroup.toString()));
            if (currentGroupAcceptedApplication != null) {
                highlightElement(graph.getEdge(currentGroupAcceptedApplication.name()));
                highlightElement(graph.getNode(currentGroupAcceptedApplication.topic().name()));
            }
        }
        checkPause();

        if (processedGroups.contains(currentGroupKey)) {
            // This group has already been processed, and there was apparently no better distribution, so we break the recursion here.

            if (verbose) System.out.println("Already processed group: " + currentGroupKey);

            // if we hit the first group that is on this path of the recursion we can do a circle swap
            if (initialApplication.getGroupAndCollectionKey().equals(currentGroupKey)) {
                System.out.println("application = " + application + ", currentGroupKey = " + currentGroupKey + ", currentPriority = " + currentPriority + ", maxPriority = " + maxPriority + ", initialApplication = " + initialApplication + ", processedGroups = " + processedGroups);
//                if (verbose) System.out.println("Found better prio for group: " + currentGroupKey);
//                //WHAT DO I WANT TO SWAP HERE. AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//                currentGroup.removeCurrentAcceptedApplication(collectionID);
//                application.acceptApplication();
//                return true;
//                var applicationsOfCurrentCroup = currentGroup.getApplicationsFromCollection(collectionID);
//            applicationsOfCurrentCroup.stream().filter(application1 -> application1.topic())
//            if (currentGroup.getCurrentAcceptedApplication(collectionID) != null && currentPriority + currentGroup.getApplicationsFromCollection(collectionID) < maxPriority) {
//                if (verbose)
//                    System.out.println("Found better prio for group: " + currentGroupKey);
//                //WHAT DO I WANT TO SWAP HERE. AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//                currentGroup.removeCurrentAcceptedApplication(collectionID);
//                applicationOfCurrentGroup.acceptApplication();
//                application.group().removeCurrentAcceptedApplication(application.collectionID());
//                application.acceptApplication();
//                return true;
//            }
            }

            return false;
        }
        processedGroups.add(currentGroupKey);
        var currentGroupPrio = currentGroup.getPriority(collectionID);
        var p = currentGroupPrio == -1 ? 1000 : currentGroupPrio;
        maxPriority += p;
        currentPriority += p;
        // Iterate over all prios of the other group that are still possible
        // we dont look at prios that are to high
        // first we only look for empty spaces
        if (verbose)
            System.out.println("Looking at group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);

        //go over all the applications of the other group.
        // TODO: 03.08.2023 Check the priority stuff
        for (Application applicationOfCurrentGroup : currentGroup.getApplicationsFromCollection(collectionID)) {
            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
            checkPause();
            // we dont do -1 here because we dont want to look at the application with prio - 1 (this is the one that lead us here)
            if (verbose)
                System.out.println("1 Looking at: " + applicationOfCurrentGroup + " of group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);
            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
            checkPause();
            // if there is space in the slot we accept it and also the original application we wanted to make space for
            if (singleAssignment(application) || multiAssignment(application)) {
                currentGroup.removeCurrentAcceptedApplication(collectionID);
                applicationOfCurrentGroup.acceptApplication();
                application.group().removeCurrentAcceptedApplication(application.collectionID());
                if (verbose) System.out.println("Swapped " + applicationOfCurrentGroup + " with " + application);
                return true;
            }
            //we couldnt swap so we paint the edge back to normal
            if (slow) unhighlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
        }
        //we didnt have any empty spaces in this groups applications, so now we do recursion from top to bottom priority
//        for (int prioOfCurrentGroup = currentGroupPrio == -1 ? 1 : currentGroupPrio; prioOfCurrentGroup + currentPriority + 1 < maxPriority && prioOfCurrentGroup < currentGroup.getApplicationsFromCollection(collectionID).size(); prioOfCurrentGroup++) {
//            var applicationOfCurrentGroup = currentGroup.getApplicationsFromCollection(collectionID).get(prioOfCurrentGroup);
//            if (verbose)
//                System.out.println("2 Looking at: " + applicationOfCurrentGroup + " of group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);
//
//            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.name());
//            checkPause();
//            // do recursion with the next group
//            // if this function returns true we know that the swap was successful and we can return true
//            // if it returns false we know that the swap was not successful and we continue with the next slot
//            if (swapGroups(applicationOfCurrentGroup, applicationOfCurrentGroup.topic().acceptedApplications().get(0).getGroupAndCollectionKey(), currentPriority, maxPriority, initialApplication, processedGroups)) {
//                application.group().removeCurrentAcceptedApplication(application.collectionID());
//                application.acceptApplication();
//                if (verbose)
//                    System.out.println("swap successful, removing old application " + application.group().getCurrentAcceptedApplication(application.collectionID()) + " and accepting new application " + application);
//                checkPause();
//                return true;
//            }
//            //we couldnt swap so we paint the edge back to normal
//            if (slow) unhighlightElement(graph.getEdge(applicationOfCurrentGroup.name());
//            checkPause();
//        }
        if (slow) {
            unhighlightElement(graph.getNode(currentGroup.toString()));
            if (currentGroupAcceptedApplication != null) {
                unhighlightElement(graph.getEdge(currentGroupAcceptedApplication.toString()));
            }
        }
        return false;
    }

}