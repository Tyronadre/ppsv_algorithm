package de.henrik.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Topic {
    private final String name;
    private final Course course;
    private final IntegerTupel slotSize;

    private final List<Slot> slots = new ArrayList<>();

    public Topic(String name, Course course, IntegerTupel slotSize, int slots) {
        this.name = name;
        this.course = course;
        this.slotSize = slotSize;
        for (int i = 0; i < slots; i++) {
            this.slots.add(new Slot(slotSize, i));
        }
    }

    public String name() {
        return name;
    }

    public Course course() {
        return course;
    }

    public IntegerTupel slotSize() {
        return slotSize;
    }

    public List<Slot> slots() {
        return slots;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Topic) obj;
        return Objects.equals(this.name, that.name) && Objects.equals(this.course, that.course) && Objects.equals(this.slotSize, that.slotSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, course, slotSize);
    }

    @Override
    public String toString() {
        return "Topic[" + "name=" + name + ", " + "course=" + course + ", " + "participants=" + slotSize + ", " + "slots=" + slots.size() + " * slotSize=" + slotSize + ']';
    }

    /**
     * Checks if the given application can be accepted
     *
     * @param application the application to check
     * @return true if the application can be accepted, false otherwise
     */
    public boolean testApplication(Application application) {
        return slots.stream().anyMatch(slot -> slot.spaceLeft() >= application.size());
    }

    /**
     * Returns a list of all possible slots for the given application
     *
     * @param application the application to check
     * @return a list of all possible slots for the given application
     */
    public List<Integer> possibleSlots(Application application) {
        var l = new ArrayList<Integer>();
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).spaceLeft() >= application.size()) {
                l.add(i);
            }
        }
        return l;
    }

    /**
     * Accepts the application in the first possible slot
     *
     * @param application the application to accept
     */
    public void acceptApplication(Application application) {
        acceptApplication(application, possibleSlots(application).get(0));
    }

    /**
     * Accepts the application in the slot with the given ID
     *
     * @param application the application to accept
     * @param slotID      the ID of the slot to accept the application in
     */
    public void acceptApplication(Application application, int slotID) {
        slots.get(slotID).acceptApplication(application);
    }

    /**
     * @return the number of participants in all slots
     */
    public int currentParticipants() {
        return slots.stream().mapToInt(Slot::participants).sum();
    }

    public List<Application> acceptedApplications() {
        return slots.stream().collect(ArrayList::new, (list, slot) -> list.addAll(slot.acceptedApplications()), ArrayList::addAll);
    }

    public void clearApplications() {
        slots.forEach(Slot::clearApplications);
    }
}
