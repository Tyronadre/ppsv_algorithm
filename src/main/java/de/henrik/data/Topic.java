package de.henrik.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Topic {
    private final String name;
    private final Course course;
    private final IntegerTupel participants;
    private final int slots;

    static List<Application> acceptedApplications = new ArrayList<>();


    public Topic(String name, Course course, IntegerTupel participants, int slots) {
        this.name = name;
        this.course = course;
        this.participants = participants;
        this.slots = slots;
    }

    public String name() {
        return name;
    }

    public Course course() {
        return course;
    }

    public IntegerTupel participants() {
        return participants;
    }

    public int slots() {
        return slots;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Topic) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.course, that.course) &&
                Objects.equals(this.participants, that.participants) &&
                this.slots == that.slots;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, course, participants, slots);
    }

    @Override
    public String toString() {
        return "Topic[" +
                "name=" + name + ", " +
                "course=" + course + ", " +
                "participants=" + participants + ", " +
                "slots=" + slots + ']';
    }

    public void acceptApplication(Application application) {
        if (currentParticipants() < slots) {
            acceptedApplications.add(application);
        } else {
            throw new IllegalArgumentException("Topic can't accept application" + application + " because it is does not fit!");
        }
    }

    private int currentParticipants() {
        return acceptedApplications.stream().mapToInt(application -> application.group().students().size()).sum();
    }


}
