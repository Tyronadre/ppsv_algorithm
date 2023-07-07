package de.henrik.data;

import java.util.Collection;

public record Group(Collection<Student> students) {

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
}
