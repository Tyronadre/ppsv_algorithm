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
        return "[Application: \n" +
                "\tGroup: " + group.toString() + "\n" +
                "\tCollectionID: " + collectionID + "\n" +
                "\tPriority: " + priority + "\n" +
                "\tTopic: " + topic.toString() + "\n" +
                "]";
    }

    public boolean isAccepted() {
        return topic.acceptedApplications().contains(this);
    }

    public void acceptApplication() {
        topic.acceptApplication(this);
    }

    public void removeApplication() {
        topic.removeApplication(this);
    }
}
