package de.henrik.generator;

import de.henrik.data.*;

import java.util.*;

public class ApplicationsProvider {
    public final long seed;
    private final ConcurrentApplicationHashMap applicationsHashMap;
    private final Map<Integer, Map<Integer, Integer>> applicationsPerGroupSizePerCollection;

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
                var topicsForGroup = new TreeMap<Group, List<Topic>>(Comparator.comparing(Group::toString));
                for (Group group : possibleGroups) {
                    for (Topic topic : topicByAtMinSize.get(groupSize)) {
                        if (applicationsHashMap.containsTopic(topic) && applicationsHashMap.getByTopic(topic).stream().anyMatch(application -> application.group().equals(group))) {
                            continue;
                        }
                        topicsForGroup.computeIfAbsent(group, k -> new ArrayList<>()).add(topic);
                    }
                }
                for (int i = 0; i < applicationsPerCollection.get(groupSize); i++) {
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
                    applicationsHashMap.add(new Application(group, topic, collectionID, prio == null ? 1 : prio.size() + 1));
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