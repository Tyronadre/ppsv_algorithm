package de.henrik.data;

import java.util.List;

public final class Group {
    private static int idCounter = 0;
    private final int ID;
    private final List<Student> students;
    private final List<Application> applications;

    public Group(List<Student> students, List<Application> applications) {
        this.students = students;
        this.applications = applications;
        ID = idCounter++;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Group{");
        for (var student : students) {
            s.append(student.name()).append(", ");
        }
        s.delete(s.length() - 2, s.length());
        s.append("}");
        return s.toString();
    }

    @Override
    public int hashCode() {
        return students.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return this.ID == group.ID;
    }

    public void removeCurrentAcceptedApplication(int collectionID) {
        for (var app : applications) {
            if (app.collectionID() == collectionID && app.isAccepted()) {
                app.removeApplication();
            }
        }
    }

    /**
     * @param collectionID
     * @return
     */
    public int getPriority(int collectionID) {
        for (var app : applications) {
            if (app.collectionID() == collectionID && app.isAccepted()) {
                return app.priority();
            }
        }
        return -1;
    }

    /**
     * @param collectionID
     * @return the accepted application or null if there is none
     */
    public Application getAcceptedApplication(int collectionID) {
        for (var app : applications) {
            if (app.collectionID() == collectionID && app.isAccepted()) {
                return app;
            }
        }
        return null;
    }

    /**
     * @return the number of collections of this group or -1 if there is none
     */
    public int getCollectionSize() {
        return applications.stream().mapToInt(Application::collectionID).max().orElse(-1);
    }

    public List<Application> getApplicationsFromCollection(int collectionID) {
        return applications.stream().filter(app -> app.collectionID() == collectionID).toList();
    }

    public Application getCurrentAcceptedApplication(int collectionID) {
        for (var app : applications) {
            if (app.collectionID() == collectionID && app.isAccepted()) {
                return app;
            }
        }
        return null;
    }

    public List<Student> students() {
        return students;
    }

    public List<Application> applications() {
        return applications;
    }

}
