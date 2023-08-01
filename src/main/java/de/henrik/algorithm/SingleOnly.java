package de.henrik.algorithm;

import de.henrik.data.*;
import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;

import java.util.*;

import static de.henrik.algorithm.Util.highlightElement;
import static de.henrik.algorithm.Util.unhighlightElement;


public class SingleOnly extends Algorithm {

    HighestPriorityAlgorithm highestPriorityAlgorithm;
    public SingleOnly(long seed, Provider provider, Graph graph) {
        super(seed, provider, graph);
    }

    @Override
    void startAlgorithm() {
        //do a highest priority first approach
        System.out.println("Starting Highest Priority Algorithm for initial assignment");
        highestPriorityAlgorithm = new HighestPriorityAlgorithm(seed, provider, graph);
        highestPriorityAlgorithm.setVerbose(verbose);
        highestPriorityAlgorithm.setSlow(slow);
        highestPriorityAlgorithm.startAlgorithm();

        highestPriorityAlgorithm = null;


        System.out.println("Starting Single Only Algorithm");
        List<Topic> topics = provider.courseAndTopicProvider.getTopicList();
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

                        //PaintApplication we are looking at
                        if (verbose) System.out.println("Testing application " + application);
                        if (slow) highlightElement(graph.getEdge(application.toString()));
                        checkPause();

                        var currentAppOfOtherGroup = application.topic().acceptedApplications().size() > 0 ? application.topic().acceptedApplications().get(0) : null;
                        if (currentAppOfOtherGroup == null) {
                            //assign since this is empty
                            if (group.getAcceptedApplication(collectionID) != null) {
                                group.removeCurrentAcceptedApplication(collectionID);
                                applicationHashMap.removeAllWithSameKey(application);
                            }
                            application.topic().acceptApplication(application);
                            improvementMade = true;
                            break;
                        } else {
                            // wir wollen jetzt rekursiv folgendes machen:
                            // die möglichen applications der anderen gruppe angucken mit de-r wir hier tauschen wollen
                            // dies gehen wir dabei von der niedrigesten zur höchsten priorität durch
                            // finden wir eine stelle an der wir diese andere gruppe schieben können sodass die priorität insgesamt kleiner ist als zum aktuellen zeitpunkt machen wir das und beenden die rekursion
                            // sonst rufen wir rekursiv wieder diese funktion auf, mit der anderen gruppe als aktuelle gruppe.
                            // die rekursion ist außerdem beendet, wenn wir wieder auf eine gruppe stoßen die wir bereits abgehandelt haben.
                            // wenn die aktuelle gruppe keine prio hat geben wir eine sehr große zahl an damit sie wenn möglich eine bekommt.
                            if (swapGroups(application, application.topic().acceptedApplications().get(0).getGroupAndCollectionKey(), 0, currentPriority == -1 ? 1000 : currentPriority, application, new HashSet<>())) {
                                improvementMade = true;
                                if (currentPriority != -1)
                                    group.getApplicationsFromCollection(collectionID).get(currentPriority - 1).removeApplication();
                                else {
                                    applicationHashMap.removeAllWithSameKey(application);
                                }
                                System.out.println("Found better distribution for " + application);
                                break;
                            } else {
                                if (verbose) System.out.println("swapGroups returned false");
                                if (slow) Util.repaintGraph(graph);
                            }
                        }
                    }
                    if (slow) Util.repaintGraph(graph);
                    checkPause();
                }
                if (slow) Util.repaintGraph(graph);
            }
            if (slow) Util.repaintGraph(graph);
        } while (improvementMade);
        Util.repaintGraph(graph);
    }

    /**
     * Gets the currently accepted group for a topic
     *
     * @param topic the topic to get the group for
     * @return the group that is currently accepted for the topic
     */
    private Group getGroupForTopic(Topic topic) {
        return topic.acceptedApplications().size() > 0 ? topic.acceptedApplications().get(0).group() : null;
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
                highlightElement(graph.getEdge(currentGroupAcceptedApplication.toString()));
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
        for (int prioOfCurrentGroup = currentGroupPrio == -1 ? 1 : currentGroupPrio; prioOfCurrentGroup + currentPriority + 1 < maxPriority && prioOfCurrentGroup < currentGroup.getApplicationsFromCollection(collectionID).size(); prioOfCurrentGroup++) {
            // we dont do -1 here because we dont want to look at the application with prio - 1 (this is the one that lead us here)
            var applicationOfCurrentGroup = currentGroup.getApplicationsFromCollection(collectionID).get(prioOfCurrentGroup);
            if (verbose)
                System.out.println("1 Looking at: " + applicationOfCurrentGroup + " of group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);
            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.toString()));
            checkPause();
            // if there is space in the slot we accept it and also the original application we wanted to make space for
            if (applicationOfCurrentGroup.topic().testApplication(applicationOfCurrentGroup)) {
                currentGroup.removeCurrentAcceptedApplication(collectionID);
                applicationOfCurrentGroup.acceptApplication();
                application.group().removeCurrentAcceptedApplication(application.collectionID());
                application.acceptApplication();
                if (verbose) System.out.println("Swapped " + applicationOfCurrentGroup + " with " + application);
                return true;
            }
            //we couldnt swap so we paint the edge back to normal
            if (slow) unhighlightElement(graph.getEdge(applicationOfCurrentGroup.toString()));
        }
        //we didnt have any empty spaces in this groups applications, so now we do recursion from top to bottom priority
        for (int prioOfCurrentGroup = currentGroupPrio == -1 ? 1 : currentGroupPrio; prioOfCurrentGroup + currentPriority + 1 < maxPriority && prioOfCurrentGroup < currentGroup.getApplicationsFromCollection(collectionID).size(); prioOfCurrentGroup++) {
            var applicationOfCurrentGroup = currentGroup.getApplicationsFromCollection(collectionID).get(prioOfCurrentGroup);
            if (verbose)
                System.out.println("2 Looking at: " + applicationOfCurrentGroup + " of group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);

            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.toString()));
            checkPause();
            // do recursion with the next group
            // if this function returns true we know that the swap was successful and we can return true
            // if it returns false we know that the swap was not successful and we continue with the next slot
            if (swapGroups(applicationOfCurrentGroup, applicationOfCurrentGroup.topic().acceptedApplications().get(0).getGroupAndCollectionKey(), currentPriority, maxPriority, initialApplication, processedGroups)) {
                application.group().removeCurrentAcceptedApplication(application.collectionID());
                application.acceptApplication();
                if (verbose)
                    System.out.println("swap successful, removing old application " + application.group().getCurrentAcceptedApplication(application.collectionID()) + " and accepting new application " + application);
                checkPause();
                return true;
            }
            //we couldnt swap so we paint the edge back to normal
            if (slow) unhighlightElement(graph.getEdge(applicationOfCurrentGroup.toString()));
            checkPause();
        }
        if (slow) {
            unhighlightElement(graph.getNode(currentGroup.toString()));
            if (currentGroupAcceptedApplication != null) {
                unhighlightElement(graph.getEdge(currentGroupAcceptedApplication.toString()));
            }
        }
        return false;
    }

    @Override
    public synchronized void setSlow(boolean slow) {
        if (highestPriorityAlgorithm != null) highestPriorityAlgorithm.setSlow(slow);
        super.setSlow(slow);
    }

    @Override
    public synchronized void setVerbose(boolean verbose) {
        if (highestPriorityAlgorithm != null) highestPriorityAlgorithm.setVerbose(verbose);
        super.setVerbose(verbose);
    }
}



// TODO: 15.07.2023 COLLECTIONS
