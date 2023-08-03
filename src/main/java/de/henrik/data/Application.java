package de.henrik.data;

import java.util.Objects;

public final class Application {
    private static int ID = 0;
    private final Group group;
    private final Topic topic;
    private final int collectionID;
    private final int priority;
    private final int id;

    protected Application(Group group, Topic topic, int collectionID, int priority, int id) {
        this.group = group;
        this.topic = topic;
        this.collectionID = collectionID;
        this.priority = priority;
        this.id = id;
    }

    public Application(Group group, Topic topic, int collectionID, int priority) {
        this(group, topic, collectionID, priority, ID++);
    }

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

    public String name() {
        return "Application" + id;
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

    public Group group() {
        return group;
    }

    public Topic topic() {
        return topic;
    }

    public int collectionID() {
        return collectionID;
    }

    public int priority() {
        return priority;
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Application) obj;
        return Objects.equals(this.group, that.group) &&
                Objects.equals(this.topic, that.topic) &&
                this.collectionID == that.collectionID &&
                this.priority == that.priority &&
                this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, topic, collectionID, priority, id);
    }

}
