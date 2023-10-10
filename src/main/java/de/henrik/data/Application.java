package de.henrik.data;

import java.util.Objects;
import java.util.Queue;

public final class Application {
    private static int idCounter = 0;
    private final Group group;
    private final Topic topic;
    private final int collectionID;
    private final int priority;
    private final int ID = idCounter++;
    private final Tupel<Group,Integer> groupAndCollectionKey;

    public Application(Group group, Topic topic, int collectionID, int priority) {
        this.group = group;
        this.topic = topic;
        this.collectionID = collectionID;
        this.priority = priority;
        this.groupAndCollectionKey = new Tupel<>(group, collectionID);
    }


    public Tupel<Group, Integer> getGroupAndCollectionKey() {
        return groupAndCollectionKey;
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
        return "Application" + ID;
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
        return ID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Application) obj;
        return this.ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, topic, collectionID, priority, ID);
    }

}
