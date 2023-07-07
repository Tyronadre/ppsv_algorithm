package de.henrik.data;

public record Application(Group group, Topic topic, int collectionID, int priority) {
    public Tupel<Group, Integer> getGroupAndCollectionKey() {
        return new Tupel<>(group, collectionID);
    }

    public int size() {
        return group.students().size();
    }


    @Override
    public String toString() {
        return "Application{" +
                "group=" + group +
                ", topic=" + topic.name() +
                ", collectionID=" + collectionID +
                ", priority=" + priority +
                '}';
    }
}
