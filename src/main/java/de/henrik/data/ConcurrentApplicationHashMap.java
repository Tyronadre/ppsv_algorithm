package de.henrik.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class ConcurrentApplicationHashMap {

    TreeMap<Integer, List<Application>> bySize ;
    TreeMap<Topic, List<Application>> byTopic ;
    TreeMap<Tupel<Group, Integer>, List<Application>> byKey ;

    public ConcurrentApplicationHashMap() {
        byKey = new TreeMap<>(Comparator.comparing(Tupel::hashCode));
        bySize = new TreeMap<>();
        byTopic = new TreeMap<>(Comparator.comparing(Topic::name));
    }

    public List<Application> getBySize(int size) {
        if (!bySize.containsKey(size)) {
            return null;
        }
        return bySize.get(size);
    }

    public List<Application> getByUpToSize(int maxSize) {
        var result = new ArrayList<Application>();
        for (List<Application> applications : bySize.values()) {
            for (Application application : applications) {
                if (application.size() <= maxSize) {
                    result.add(application);
                }
            }
        }
        return result;
    }

    public List<Application> getByTopic(Topic topic) {
        if (byTopic.size() == 0 || !byTopic.containsKey(topic)) {
            return null;
        }
        return new ArrayList<>(byTopic.get(topic));
    }

    public TreeMap<Tupel<Group, Integer>, List<Application>> getByKey() {
        return byKey;
    }

    public List<Application> getByKey(Tupel<Group, Integer> groupAndCollectionKey) {
        if (byKey.size() == 0 || !byKey.containsKey(groupAndCollectionKey)) {
            return null;
        }
        return new ArrayList<>(byKey.get(groupAndCollectionKey));
    }

    public List<Application> getByTopicAndSize(Topic topic, int size) {
        if (!byTopic.containsKey(topic)) {
            return null;
        }
        if (!bySize.containsKey(size)) {
            return null;
        }
        var list = byTopic.get(topic);
        list.retainAll(bySize.get(size));
        return list;
    }

    public List<Application> getByTopicAndUpToSize(Topic topic, int maxSize) {
        if (!byTopic.containsKey(topic)) {
            return null;
        }
        var result = new ArrayList<Application>();
        for (Application application : byTopic.get(topic)) {
            if (application.size() <= maxSize) {
                result.add(application);
            }
        }
        return result;
    }

    /**
     * Removes the given application from the map
     *
     * @param application the application to remove
     */
    public void remove(Application application) {
        bySize.values().forEach(applications -> applications.remove(application));
        byTopic.values().forEach(applications -> applications.remove(application));
        byKey.values().forEach(applications -> applications.remove(application));
    }

    /**
     * Removes all applications with the same groupAndCollectionKey from the map
     *
     * @param application the application to remove
     */
    public void removeAllWithSameKey(Application application) {
        bySize.get(application.size()).removeIf(application::equals);
        byTopic.get(application.topic()).removeIf(application::equals);
        byKey.get(application.getGroupAndCollectionKey()).removeIf(application::equals);
    }

    public void add(Application application) {
        bySize.computeIfAbsent(application.size(), k -> new ArrayList<>()).add(application);
        byTopic.computeIfAbsent(application.topic(), k -> new ArrayList<>()).add(application);
        byKey.computeIfAbsent(application.getGroupAndCollectionKey(), k -> new ArrayList<>()).add(application);
    }

    public void addAll(List<Application> applications) {
        for (var application : applications) {
            add(application);
        }
    }

    public boolean containsTopic(Topic topic) {
        if (byTopic.size() == 0) {
            return false;
        }
        return byTopic.containsKey(topic);
    }

    public List<Application> getApplicationList() {
        var result = new ArrayList<Application>();
        for (var applications : bySize.values()) {
            result.addAll(applications);
        }
        return result;
    }

    public double getKeySize() {
        return byKey.keySet().size();
    }
}
