package de.henrik.algorithm;

import de.henrik.data.*;

import java.util.*;

import static de.henrik.Main.graph;
import static de.henrik.Main.provider;
import static de.henrik.algorithm.Util.*;


public class TTCGroupsNew extends Algorithm {

    private ConcurrentApplicationHashMap applicationHashMap;

    public TTCGroupsNew(long seed) {
        super(seed);
    }

    public static HashMap<Integer, Integer> depthHits = new HashMap<>();

    @Override
    @SuppressWarnings("Duplicates")
    void startAlgorithm() {
        //do a highest priority first approach
        System.out.println("Starting Highest Priority Algorithm for initial assignment");
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

                    //if already perfect prio we can skip this collection
                    int currentPriority = group.getPriority(collectionID);
                    if (currentPriority == 1) {
                        continue;
                    }
                    if (verbose)
                        System.out.println("\nTrying to find a better application for" + group + " with current " + group.getAcceptedApplication(collectionID));

                    // go through all applications of the collection of this group and find one made out of non accepted applications
                    for (Application application : group.getApplicationsFromCollection(collectionID)) {
                        // if we didn't find any better assignment we stop
                        if (application.priority() == currentPriority) {
                            if (verbose)
                                System.out.println("Did not find a better application for " + group + " in collection " + collectionID);
                            break;
                        }

                        // PaintApplication we are looking at
                        if (verbose) System.out.println("Testing application " + application);
                        if (slow) highlightElement(graph.getEdge(application.name()));
                        checkPause();

                        // Test if we can assign this application directly because there is space, or we can do a multi assignment to this slot.
                        if (singleAssignment(application) || multiAssignment(application, null)) {
                            applicationHashMap.removeAllWithSameKey(application);
                            improvementMade = true;
                            if (verbose)
                                System.out.println("Found better assignment for " + group + " in collection " + collectionID);
                            break;
                        }

                        if (slow) unhighlightElement(graph.getEdge(application.name()));

                    }
                    if (slow) Util.repaintGraph();
                    checkPause();
                }
                if (slow) Util.repaintGraph();
            }
            if (slow) Util.repaintGraph();
            Score.score(provider);
        } while (improvementMade);
        Util.repaintGraph();
    }

    /**
     * Tries to assign the application together with other not assigned applications to a slot with enough space.
     *
     * @param application           The application we want to assign
     * @param forbiddenApplications Applications that will be ignored by this method
     * @return true if we found a valid assignment, false otherwise
     */
    private boolean multiAssignment(Application application, List<Tupel<Group, Integer>> forbiddenApplications) {
        // Check if we can find enough unassigned applications that fit in this slot together with the current application
        // its a bit of a waste to compute this every time, but i want to do it step by step first
        var topic = application.topic();
        if (slow) highlightElement(graph.getNode(topic.name()));
        checkPause();
        var possibleApplications = applicationHashMap.getByTopic(topic);
        possibleApplications.remove(application);
        if (forbiddenApplications != null)
            possibleApplications.removeIf(application1 -> forbiddenApplications.contains(application1.getGroupAndCollectionKey()));
        if (slow) possibleApplications.forEach(application1 -> highlightElement(graph.getEdge(application1.name())));
        checkPause();

        for (var slotID : topic.possibleSlots(application)) {
            var currentParticipants = topic.slots().get(slotID).participants();
            var possibleApplicationsForSlot = multiObjectiveKnapsack(possibleApplications, topic.slotSize().second() - currentParticipants - application.size());
            if (!possibleApplicationsForSlot.isEmpty()) {
                if (slow)
                    possibleApplicationsForSlot.forEach(application1 -> highlightElement2(graph.getEdge(application1.name())));
                checkPause();

                application.topic().acceptApplication(application, slotID);
                applicationHashMap.removeAllWithSameKey(application);
                for (var app : possibleApplicationsForSlot) {
                    applicationHashMap.removeAllWithSameKey(app);
                    application.topic().acceptApplication(app, slotID);
                }
                return true;
            }
        }
        if (slow) unhighlightElement(graph.getNode(topic.name()));
        if (slow) possibleApplications.forEach(application1 -> unhighlightElement(graph.getEdge(application1.name())));
        return false;
    }

    /**
     * Tries to assign the application to a slot.
     *
     * @param application the application to assign
     * @return true if it was assigned, false if not
     */
    private boolean singleAssignment(Application application) {
        var topic = application.topic();
        if (slow) highlightElement(graph.getNode(topic.name()));
        checkPause();

        // Check if we can assign this, because there is free space and we can fill minSize (i dont think this will ever happen)
        for (var slotID : topic.possibleSlots(application)) {
            var currentParticipants = topic.slots().get(slotID).participants();
            if (currentParticipants + application.size() <= topic.slotSize().second() && currentParticipants + application.size() >= topic.slotSize().first()) {
                application.topic().acceptApplication(application, slotID);
                applicationHashMap.removeAllWithSameKey(application);
                return true;
            }
        }
        if (slow) unhighlightElement(graph.getNode(topic.name()));
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
    private boolean swapGroups(Application application, Tupel<Group, Integer> currentGroupKey, int currentPriority, int maxPriority, Application initialApplication, Set<Tupel<Group, Integer>> processedGroups, int depth) {
        // TODO: 17.07.2023 i think we can vastly optimize this if we save the groups that cannot improve in this iteration, and dont look at them again.

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


        var currentGroupPrio = currentGroup.getPriority(collectionID);
        var p = currentGroupPrio == -1 ? 1000 : currentGroupPrio;
        maxPriority += p;
        currentPriority += application.priority();

        if (processedGroups.contains(currentGroupKey)) {
            // This group has already been processed, and there was apparently no better distribution, so we break the recursion here.

            if (verbose) System.out.println("Already processed group: " + currentGroupKey);

            // if we hit the first group that is on this path of the recursion we can do a circle swap
//            if (initialApplication.getGroupAndCollectionKey().equals(currentGroupKey)) {
//                if (maxPriority > currentPriority) {
//                    System.out.println("Found circle swap");
//                    if (verbose) System.out.println("Found better prio for group: " + currentGroupKey);
//                    currentGroup.removeCurrentAcceptedApplication(collectionID);
//                    application.group().removeCurrentAcceptedApplication(application.collectionID());
//                    application.acceptApplication();
//
//                    saveDepth(depth);
//                    return true;
//                }
//                if (verbose)
//                    System.out.println("max depth: \napplication = " + application + ", currentGroupKey = " + currentGroupKey + ", currentPriority = " + currentPriority + ", maxPriority = " + maxPriority + ", initialApplication = " + initialApplication + ", numberOfProcessedGroups = " + processedGroups.size());
//            }

            saveDepth(depth);
            return false;
        }
        processedGroups.add(currentGroupKey);





        // Iterate over all prios of the other group that are still possible
        // we dont look at prios that are to high
        // first we only look for empty spaces
        if (verbose)
            System.out.println("Looking at group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);
        //go over all the applications of the other group.
        // TODO: 03.08.2023 Check the priority stuff
        for (Application applicationOfCurrentGroup : currentGroup.getApplicationsFromCollection(collectionID)) {
            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
            if (verbose)
                System.out.println("1 Looking at: " + applicationOfCurrentGroup + " of group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);
            checkPause();
            // if there is space in the slot we accept it and also the original application we wanted to make space for
            if (singleAssignment(applicationOfCurrentGroup) || multiAssignment(applicationOfCurrentGroup, new ArrayList<>(Collections.singleton(application.getGroupAndCollectionKey())))) {
                application.topic().removeApplication(currentGroup);
                application.group().removeCurrentAcceptedApplication(application.collectionID());
                application.acceptApplication();
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

    private void saveDepth(int depth) {
        depthHits.putIfAbsent(depth, 1);
        depthHits.computeIfPresent(depth, (k, v) -> v + 1);
    }
}