package de.henrik.generator;

import de.henrik.data.*;

import java.util.*;

public class ApplicationsProvider {
    public final long seed;
    protected final List<Application> applicationsList;
    protected ConcurrentApplicationHashMap applicationsHashMap;
    protected final Map<Integer, Map<Integer, Integer>> applicationsPerGroupSizePerCollection;
    protected final boolean precalc;

    /**
     * @param seed                                  the seed
     * @param applicationsPerGroupSizePerCollection a map how many applications per group should be generated per collection of that group. this should be possible with the provided number of topics and students, or this providers generate will fail eventually
     */
    public ApplicationsProvider(long seed, Map<Integer, Map<Integer, Integer>> applicationsPerGroupSizePerCollection, boolean precalc) {
        this.seed = seed;
        this.applicationsPerGroupSizePerCollection = applicationsPerGroupSizePerCollection;
        applicationsList = new ArrayList<>();
        applicationsHashMap = new ConcurrentApplicationHashMap();
        this.precalc = precalc;
    }

    public List<Application> getApplicationList() {
        return new ArrayList<>(applicationsList);
    }

    /**
     * Generates all applications
     *
     * @param groupsBySize     Key: Group Size; Value: A list with all groups with this size
     * @param topicByAtMinSize Key: Size; Value: A list with all topics with >= size
     */
    public void generate(Map<Integer, List<Group>> groupsBySize, Map<Integer, List<Topic>> topicByAtMinSize) {
        System.out.println("Generating applications: ");
        applicationsPerGroupSizePerCollection.forEach((collectionID, applicationsPerGroupSize) -> {
            System.out.println("Collection " + collectionID + ":");
            applicationsPerGroupSize.forEach((groupSize, applications) -> {
                System.out.println("  Group size " + groupSize + ": " + applications + " applications");
            });
        });

        Random random = new Random(seed);
        var groupsWithCollection = new TreeMap<Integer, List<Group>>();
        TreeMap<Group, List<Topic>> topicsForGroup = null;
        if (precalc) {

            System.out.println("Precalculating topics for groups...");
            topicsForGroup = new TreeMap<>(Comparator.comparing(Group::toString));
            for (int groupSize : topicByAtMinSize.keySet()) {
                var topics = topicByAtMinSize.get(groupSize);
                for (Group group : groupsBySize.get(groupSize)) {
                    topicsForGroup.put(group, new ArrayList<>(topics));
                }
            }
            System.out.println("Precalculating topics for groups done!");

        }

        int count = 0;
        int maxCount = applicationsPerGroupSizePerCollection.values().stream().mapToInt(value -> value.values().stream().mapToInt(value1 -> value1).sum()).sum();
        for (int collectionID : applicationsPerGroupSizePerCollection.keySet()) {
            groupsWithCollection.computeIfAbsent(collectionID, k -> new ArrayList<>());
            var applicationsPerCollection = applicationsPerGroupSizePerCollection.get(collectionID);
            for (int groupSize : applicationsPerCollection.keySet()) {
                //Get all possible groups for this size and collection number
                var possibleGroups = groupsBySize.get(groupSize);
                if (collectionID != 1) {
                    possibleGroups.retainAll(groupsWithCollection.get(collectionID - 1));
                }

                var possibleTopics = topicByAtMinSize.get(groupSize);

                for (int i = 0; i < applicationsPerCollection.get(groupSize); i++) {
                    System.out.print("Generating application " + count++ + "/" + maxCount + "\r");

                    if (precalc && topicsForGroup.isEmpty()) {
                        System.err.println("Not enough topics/groups! Generation Stopped!");
                        break;
                    }

                    Group group = possibleGroups.get(random.nextInt(possibleGroups.size()));
                    Topic topic;
                    if (precalc) {

                        topic = topicsForGroup.get(group).get(random.nextInt(topicsForGroup.get(group).size()));
                        topicsForGroup.get(group).remove(topic);
                        if (topicsForGroup.get(group).isEmpty()) {
                            topicsForGroup.remove(group);
                            possibleGroups.remove(group);
                        }

                    } else {
                        topic = possibleTopics.get(random.nextInt(possibleTopics.size()));
                    }


                    var prio = applicationsHashMap.getByKey(new Tupel<>(group, collectionID));
                    var app = new Application(group, topic, collectionID, prio == null ? 1 : prio.size() + 1);
                    applicationsHashMap.add(app);
                    group.applications().add(app);
                    if (!groupsWithCollection.get(collectionID).contains(group)) {
                        groupsWithCollection.get(collectionID).add(group);
                    }
                }
            }
        }
        applicationsList.addAll(applicationsHashMap.getApplicationList());
        System.out.println("Done!");
    }

    /**
     * @return the applicationsHashMap. This will be subject to all changes that will be made by any sources until the clean method is called.
     */
    public ConcurrentApplicationHashMap getConcurrentApplicationHashMap() {
        return applicationsHashMap;
    }

    public void clear() {
        applicationsHashMap = new ConcurrentApplicationHashMap();
        applicationsHashMap.addAll(applicationsList);
    }
}