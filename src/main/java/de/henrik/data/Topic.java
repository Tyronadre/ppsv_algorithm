package de.henrik.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Topic {
    private final String name;
    private final Course course;
    private final IntegerTupel slotSize;

    private final List<Slot> slots = new ArrayList<>();
    private static int idCounter = 0;
    private final int ID = idCounter++;

    public Topic(String name, Course course, IntegerTupel slotSize, int slots) {
        this.name = name;
        this.course = course;
        this.slotSize = slotSize;
        for (int i = 0; i < slots; i++) {
            this.slots.add(new Slot(slotSize, i));
        }
    }

    public String name() {
        return course.name() + name;
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
        return this.ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, course, slotSize);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Topic[" + "name=").append(name).append(", Slots:\n\t\t ");
        for (var slot : slots) {
            s.append("ID ").append(slot.ID()).append(": ").append(slot.participants()).append(" (");
            for (var application : slot.applications) {
                s.append(application.getGroupAndCollectionKey());
                if (slot.applications.indexOf(application) != slot.applications.size() - 1) {
                    s.append(", ");
                }
            }
            s.append(")");
            s.append("\t");
        }
        s.append(" ]");
        return s.toString();
    }

    /**
     * Checks if the given application can be accepted
     * This will return true as long as there is enough space
     * This will also return true if the minSize of this topic is not met
     * This will also return true if the application is already accepted
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
        List<Application> acceptedApplications = new ArrayList<>();
        for (Slot slot : slots) {
            acceptedApplications.addAll(slot.acceptedApplications());
        }
        return acceptedApplications;
    }


    /**
     * Returns the slot of the given application or null if the application is not accepted
     *
     * @param application the application to check
     * @return the slot of the given application or null if the application is not accepted
     */
    public Slot getSlotOfApplication(Application application) {
        for (var slot : slots) {
            if (slot.applications.contains(application)) {
                return slot;
            }
        }
        return null;
    }

    public void clearApplications() {
        slots.forEach(Slot::clearApplications);
    }

    public void removeApplication(Application currentAppOfGroup) {
        slots.forEach(slot -> slot.removeApplication(currentAppOfGroup));
    }

    public void removeApplication(Group group) {
        slots.forEach(slot -> slot.removeApplication(group));
    }
}
