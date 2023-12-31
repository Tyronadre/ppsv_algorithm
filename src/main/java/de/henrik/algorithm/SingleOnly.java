package de.henrik.algorithm;

import de.henrik.data.*;

import java.util.*;

import static de.henrik.Main.graph;
import static de.henrik.Main.provider;
import static de.henrik.algorithm.Util.highlightElement;
import static de.henrik.algorithm.Util.unhighlightElement;


public class SingleOnly extends Algorithm {

    public SingleOnly(long seed) {
        super(seed);
    }

    public static HashMap<Integer, Integer> depthHits = new HashMap<>();

    @Override
    void startAlgorithm() {
        //do a highest priority first approach
        System.out.println("Starting Highest Priority Algorithm for initial assignment");
//        new HighestPriorityAlgorithm(seed).startAlgorithm();

//        highestPriorityAlgorithm = null;


        System.out.println("Starting Single Only Algorithm");
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
                        if (slow) highlightElement(graph.getEdge(application.name()));
                        checkPause();

                        var currentAppOfOtherGroup = !application.topic().acceptedApplications().isEmpty() ? application.topic().acceptedApplications().get(0) : null;
                        if (currentAppOfOtherGroup == null) {
                            //assign since this is empty
                            if (group.getAcceptedApplication(collectionID) != null) {
                                group.removeCurrentAcceptedApplication(collectionID);
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
                            if (swapGroups(application, application.topic().acceptedApplications().get(0).getGroupAndCollectionKey(), application.priority(), currentPriority == -1 ? 1000 : currentPriority, application, new HashSet<>(Collections.singleton(application.getGroupAndCollectionKey())),0)) {
                                improvementMade = true;
                                if (currentPriority != -1)
                                    group.getApplicationsFromCollection(collectionID).get(currentPriority - 1).removeApplication();
                                else {
                                    applicationHashMap.removeAllWithSameKey(application);
                                }
                                if (verbose) System.out.println("Found better distribution for " + application);
                                break;
                            } else {
                                if (verbose) System.out.println("swapGroups returned false");
                                if (slow) Util.repaintGraph();
                            }
                        }
                    }
                    if (slow) Util.repaintGraph();
                    checkPause();
                }
                if (slow) Util.repaintGraph();
            }
            if (slow) Util.repaintGraph();
        } while (improvementMade);

        depthHits.forEach((integer, integer2) -> System.out.println("Depth: " + integer + " Hits: " + integer2));
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
            if (initialApplication.getGroupAndCollectionKey().equals(currentGroupKey)) {
                if (maxPriority > currentPriority) {
                    System.out.println("Found circle swap");
                    if (verbose) System.out.println("Found better prio for group: " + currentGroupKey);
                    currentGroup.removeCurrentAcceptedApplication(collectionID);
                    application.group().removeCurrentAcceptedApplication(application.collectionID());
                    application.acceptApplication();

                    saveDepth(depth);
                    return true;
                }
                if (verbose) System.out.println("max depth: \napplication = " + application + ", currentGroupKey = " + currentGroupKey + ", currentPriority = " + currentPriority + ", maxPriority = " + maxPriority + ", initialApplication = " + initialApplication + ", numberOfProcessedGroups = " + processedGroups.size());
            }

            saveDepth(depth);
            return false;
        }
        processedGroups.add(currentGroupKey);
        // Iterate over all prios of the other group that are still possible
        // we dont look at prios that are to high
        // first we only look for empty spaces
        if (verbose)
            System.out.println("Looking at group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);
        for (int prioOfCurrentGroup = 1; prioOfCurrentGroup + currentPriority < maxPriority && prioOfCurrentGroup < currentGroup.getApplicationsFromCollection(collectionID).size(); prioOfCurrentGroup++) {
            // we dont do -1 here because we dont want to look at the application with prio - 1 (this is the one that lead us here)
            var applicationOfCurrentGroup = currentGroup.getApplicationsFromCollection(collectionID).get(prioOfCurrentGroup - 1);
            if (verbose)
                System.out.println("1 Looking at: " + applicationOfCurrentGroup + " of group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);
            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
            checkPause();
            // if there is space in the slot we accept it and also the original application we wanted to make space for
            if (applicationOfCurrentGroup.topic().testApplication(applicationOfCurrentGroup)) {
                currentGroup.removeCurrentAcceptedApplication(collectionID);
                applicationOfCurrentGroup.acceptApplication();
                application.group().removeCurrentAcceptedApplication(application.collectionID());
                application.acceptApplication();
                if (verbose) System.out.println("Swapped " + applicationOfCurrentGroup + " with " + application);
                saveDepth(depth);
                return true;
            }
            // we couldn't swap so we paint the edge back to normal
            if (slow) unhighlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
        }
        //we didnt have any empty spaces in this groups applications, so now we do recursion from top to bottom priority
        for (int prioOfCurrentGroup = 1; prioOfCurrentGroup + currentPriority < maxPriority && prioOfCurrentGroup < currentGroup.getApplicationsFromCollection(collectionID).size(); prioOfCurrentGroup++) {
            var applicationOfCurrentGroup = currentGroup.getApplicationsFromCollection(collectionID).get(prioOfCurrentGroup - 1);
            if (verbose)
                System.out.println("2 Looking at: " + applicationOfCurrentGroup + " of group: " + currentGroup + " with currentPriority: " + currentPriority + " and maxPriority: " + maxPriority);

            if (slow) highlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
            checkPause();
            // do recursion with the next group
            // if this function returns true we know that the swap was successful and we can return true
            // if it returns false we know that the swap was not successful and we continue with the next slot
            if (swapGroups(applicationOfCurrentGroup, applicationOfCurrentGroup.topic().acceptedApplications().get(0).getGroupAndCollectionKey(), currentPriority, maxPriority, initialApplication, processedGroups,depth++)) {
                application.group().removeCurrentAcceptedApplication(application.collectionID());
                application.acceptApplication();
                if (verbose)
                    System.out.println("swap successful, removing old application " + application.group().getCurrentAcceptedApplication(application.collectionID()) + " and accepting new application " + application);
                checkPause();

                return true;
            }
            //we couldnt swap so we paint the edge back to normal
            if (slow) unhighlightElement(graph.getEdge(applicationOfCurrentGroup.name()));
            checkPause();
        }
        if (slow) {
            unhighlightElement(graph.getNode(currentGroup.toString()));
            if (currentGroupAcceptedApplication != null) {
                unhighlightElement(graph.getEdge(currentGroupAcceptedApplication.toString()));
            }
        }
        saveDepth(depth);
        return false;
    }

    private void saveDepth(int depth) {
        depthHits.putIfAbsent(depth, 1);
        depthHits.computeIfPresent(depth, (k, v) -> v + 1);
    }

}