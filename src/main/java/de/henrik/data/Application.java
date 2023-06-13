package de.henrik.data;

public record Application(Group group, Topic topic, int collectionID, int priority) {
    public Tupel<Integer, Integer> getGroupAndCollectionKey() {
        return new Tupel<>(collectionID, priority);
    }
}
