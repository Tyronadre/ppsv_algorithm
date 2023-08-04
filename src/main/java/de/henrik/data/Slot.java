package de.henrik.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Slot {
    List<Application> applications = new ArrayList<>();
    private final IntegerTupel slotSize;
    private final int ID;


    public Slot(IntegerTupel slotSize, int ID) {
        if (slotSize.first() < 0 || slotSize.second() < 0) {
            throw new IllegalArgumentException("Slot size must be positive!");
        }
        if (slotSize.first() > slotSize.second()) {
            throw new IllegalArgumentException("Slot size must be positive!");
        }
        this.slotSize = slotSize;
        this.ID = ID;
    }

    public int participants() {
        return applications.stream().mapToInt(application -> application.group().students().size()).sum();
    }

    public int spaceLeft() {
        return slotSize.second() - participants();
    }

    public boolean validSlot() {
        return participants() >= slotSize.first() && participants() <= slotSize.second();
    }

    @Override
    public String toString() {
        return "Slot{" +
                "ID=" + ID +
                ", Accepted Applications=" + applications +
                '}';
    }

    public void acceptApplication(Application application) {
        applications.add(application);
    }

    public void removeApplication(Application application) {
        applications.remove(application);
    }

    public void removeApplication(Group group) {
        applications.removeIf(application -> application.group().equals(group));
    }

    public List<Application> acceptedApplications() {
        return applications;
    }

    public void clearApplications() {
        applications.clear();
    }

    public IntegerTupel slotSize() {
        return slotSize;
    }

    public int ID() {
        return ID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Slot) obj;
        return Objects.equals(this.slotSize, that.slotSize) &&
                this.ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotSize, ID);
    }

}
