package de.henrik.generator;

import de.henrik.data.Application;
import de.henrik.data.Group;
import de.henrik.data.Student;

import java.util.*;

public class SingleStudentAndGroupProvider extends StudentAndGroupProvider {
    public SingleStudentAndGroupProvider(int studentCount) {
        super(0, studentCount, null);
    }

    @Override
    public void generate() {
        studentList = new ArrayList<>();
        groupsBySize = new TreeMap<>();
        groupsBySize.put(1, new ArrayList<>());

        for (int i = 0; i < studentCount; i++) {
            var student = new Student("Student " + i, "tu" + i + "id");
            studentList.add(student);
            var group = new Group(new ArrayList<>());
            group.students().add(student);
            groupsBySize.get(1).add(group);
        }
    }
}
