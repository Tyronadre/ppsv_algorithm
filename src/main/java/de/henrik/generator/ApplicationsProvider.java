package de.henrik.generator;

import de.henrik.data.Application;
import de.henrik.data.Group;
import de.henrik.data.Topic;
import de.henrik.data.Tupel;

import java.util.*;

public class ApplicationsProvider {
    public final long seed;
    private final Map<Integer, Map<Integer, Integer>> applicationsPerGroupSizePerCollection;

    private HashMap<Integer, List<Application>> applicationsBySize;

    public ApplicationsProvider(long seed, Map<Integer, Map<Integer, Integer>> applicationsPerGroupSizePerCollection) {
        this.seed = seed;
        this.applicationsPerGroupSizePerCollection = applicationsPerGroupSizePerCollection;
    }

    public List<Application> getApplicationList() {
        if (applicationsBySize == null) {
            throw new RuntimeException("Call generator first!");
        }
        return applicationsBySize.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
    }

    public Map<Integer, Map<Integer, Integer>> getApplicationsPerGroupSizePerCollection() {
        if (applicationsBySize == null) {
            throw new RuntimeException("Call generator first!");
        }
        return applicationsPerGroupSizePerCollection;
    }

    public Map<Integer, List<Application>> getApplicationsBySize() {
        if (applicationsBySize == null) {
            throw new RuntimeException("Call generator first!");
        }
        return applicationsBySize;
    }

    /**
     * Generates all applications
     *
     * @param groupsBySize Key: Group Size; Value: A list with all groups with this size
     * @param topicBySize  Key: Size; Value: A list with all topics with >= size
     */
    public void generate(Map<Integer, List<Group>> groupsBySize, Map<Integer, List<Topic>> topicBySize) {
        Random random = new Random(seed);
        applicationsBySize = new HashMap<>();
        var applicationsByGroupAndCollection = new HashMap<>();
        for (int collectionID : applicationsPerGroupSizePerCollection.keySet()) {
            var applicationsPerCollection = applicationsPerGroupSizePerCollection.get(collectionID);
            for (int groupSize : applicationsPerCollection.keySet()) {
                if (!applicationsBySize.containsKey(groupSize)) {
                    applicationsBySize.put(groupSize, new ArrayList<>());
                }
                List<Group> availableGroups = new ArrayList<>(groupsBySize.get(groupSize));
                List<Topic> availableTopics = new ArrayList<>(topicBySize.get(groupSize));
                for (int i = 0; i < applicationsPerCollection.get(groupSize); i++) {
                    if (availableTopics.size() == 0 || availableGroups.size() == 0) {
                        System.err.println("Not enough topics or groups!");
                        break;
                    }
                    Group group = availableGroups.remove(random.nextInt(availableGroups.size()));
                    Topic topic = availableTopics.remove(random.nextInt(availableTopics.size()));
                    Tupel<Group, Topic> key = new Tupel<>(group, topic);
                    if (!applicationsByGroupAndCollection.containsKey(key)) {
                        var list = new ArrayList<>();
                        list.add(topic);
                        applicationsByGroupAndCollection.put(key, list);
                    } else {
                        var list = (List<Topic>) applicationsByGroupAndCollection.get(key);
                        if (list.contains(topic)) {
                            i--;
                            continue;
                        } else {
                            list.add(topic);
                        }
                    }
                    applicationsBySize.get(groupSize).add(new Application(key.first(), key.second(), collectionID, ((List<Topic>) applicationsByGroupAndCollection.get(key)).size()));
                }
            }
        }
    }

//    public void generate(Map<Integer, List<Group>> groupsBySize, Map<Integer, List<Topic>> topicBySize) {
//        Random random = new Random(seed);
//        applicationsBySize = new HashMap<>();
//        var applicationsByGroupAndCollection = new HashMap<>();
//        for (int collectionID : applicationsPerGroupSizePerCollection.keySet()) {
//            var applicationsPerCollection = applicationsPerGroupSizePerCollection.get(collectionID);
//            for (int groupSize : applicationsPerCollection.keySet()) {
//                if (!applicationsBySize.containsKey(groupSize)) {
//                    applicationsBySize.put(groupSize, new ArrayList<>());
//                }
//                for (int i = 0; i < applicationsPerCollection.get(groupSize); i++) {
//                    Tupel<Group, Topic> key;
//                    do {
//                        Group group = groupsBySize.get(groupSize).get(random.nextInt(groupsBySize.get(groupSize).size()));
//                        Topic topic = topicBySize.get(groupSize).get(random.nextInt(topicBySize.get(groupSize).size()));
//                        key = new Tupel<>(group, topic);
//                        if (!applicationsByGroupAndCollection.containsKey(key)) {
//                            var list = new ArrayList<>();
//                            list.add(topic);
//                            applicationsByGroupAndCollection.put(key, list);
//                            break;
//                        } else {
//                            var list = (List<Topic>) applicationsByGroupAndCollection.get(key);
//                            if (list.contains(topic)) {
//                                continue;
//                            }else {
//                                list.add(topic);
//                                break;
//                            }
//                        }
//                    } while (true);
//                    applicationsBySize.get(groupSize).add(new Application(key.first(),key.second(), collectionID, ((List<Topic>) applicationsByGroupAndCollection.get(key)).size()));
//                }
//            }
//        }
//    }
}