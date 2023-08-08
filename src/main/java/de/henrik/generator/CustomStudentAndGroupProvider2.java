package de.henrik.generator;

import de.henrik.data.Group;
import de.henrik.data.Student;

import java.util.ArrayList;
import java.util.TreeMap;

public class CustomStudentAndGroupProvider2 extends StudentAndGroupProvider{
    public CustomStudentAndGroupProvider2() {
        super(0, 3, null);
    }

    @Override
    public void generate() {
        studentList = new ArrayList<>();
        groupsBySize = new TreeMap<>();
        groupsBySize.put(1, new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            var group = new Group(new ArrayList<>());
            groupsBySize.get(1).add(group);
            group.students().add(new Student("Student " + i + 15, "tu" + i + 15 + "id"));
        }
    }
}
