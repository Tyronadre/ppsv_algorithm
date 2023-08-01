package de.henrik.generator;

import de.henrik.data.*;

import java.util.*;

public class ApplicationsProvider {
    public final long seed;
    protected final ConcurrentApplicationHashMap applicationsHashMap;
    protected final Map<Integer, Map<Integer, Integer>> applicationsPerGroupSizePerCollection;

    /**
     * @param seed                                  the seed
     * @param applicationsPerGroupSizePerCollection a map how many applications per group should be generated per collection of that group. this should be possible with the provided number of topics and students, or this providers generate will fail eventually
     */
    public ApplicationsProvider(long seed, Map<Integer, Map<Integer, Integer>> applicationsPerGroupSizePerCollection) {
        this.seed = seed;
        this.applicationsPerGroupSizePerCollection = applicationsPerGroupSizePerCollection;
        applicationsHashMap = new ConcurrentApplicationHashMap();
    }

    public List<Application> getApplicationList() {
        return applicationsHashMap.getApplicationList();
    }

    /**
     * Generates all applications
     *
     * @param groupsBySize     Key: Group Size; Value: A list with all groups with this size
     * @param topicByAtMinSize Key: Size; Value: A list with all topics with >= size
     */
    public void generate(Map<Integer, List<Group>> groupsBySize, Map<Integer, List<Topic>> topicByAtMinSize) {
        Random random = new Random(seed);
        var groupsWithCollection = new TreeMap<Integer, List<Group>>();
        for (int collectionID : applicationsPerGroupSizePerCollection.keySet()) {
            groupsWithCollection.computeIfAbsent(collectionID, k -> new ArrayList<>());
            var applicationsPerCollection = applicationsPerGroupSizePerCollection.get(collectionID);
            for (int groupSize : applicationsPerCollection.keySet()) {
                //Get all possible groups for this size and collection number
                var possibleGroups = groupsBySize.get(groupSize);
                if (collectionID != 1) {
                    possibleGroups.retainAll(groupsWithCollection.get(collectionID - 1));
                }
                //Get all possible Topics for each group
                // TODO: 19.07.2023 Kann effizienter gemacht werden, wenn ich einmal alle ausrechne und dann rausnehme, was keine anmeldung im ersten cycle hat.
                var topicsForGroup = new TreeMap<Group, List<Topic>>(Comparator.comparing(Group::toString));
                var counter = 0;
                var maxcounter = possibleGroups.size() * topicByAtMinSize.get(groupSize).size();
                for (Group group : possibleGroups) {
                    for (Topic topic : topicByAtMinSize.get(groupSize)) {
                        if (counter++ % 100 == 0)
                            System.out.print(counter + "/" + maxcounter + "\r");
                        if (applicationsHashMap.containsTopic(topic) && applicationsHashMap.getByTopic(topic).stream().anyMatch(application -> application.group().equals(group))) {
                            continue;
                        }
                        topicsForGroup.computeIfAbsent(group, k -> new ArrayList<>()).add(topic);
                    }
                }
                System.out.println("\n");
                for (int i = 0; i < applicationsPerCollection.get(groupSize); i++) {
                    System.out.print(i + " of " + applicationsPerCollection.get(groupSize) + " for collection " + collectionID + " and group size " + groupSize + "\r");
                    if (topicsForGroup.size() == 0) {
                        System.err.println("Not enough topics/groups! Generation Stopped!");
                        break;
                    }
                    Group group = topicsForGroup.keySet().toArray(new Group[0])[random.nextInt(topicsForGroup.size())];
                    Topic topic = topicsForGroup.get(group).get(random.nextInt(topicsForGroup.get(group).size()));
                    topicsForGroup.get(group).remove(topic);
                    if (topicsForGroup.get(group).size() == 0) {
                        topicsForGroup.remove(group);
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
    }

    public ConcurrentApplicationHashMap getConcurrentApplicationHashMap() {
        ConcurrentApplicationHashMap concurrentApplicationHashMap = new ConcurrentApplicationHashMap();
        concurrentApplicationHashMap.addAll(getApplicationList());
        return concurrentApplicationHashMap;
    }
}