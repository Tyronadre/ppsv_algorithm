package de.henrik.generator;

import de.henrik.data.Application;
import de.henrik.data.Group;
import de.henrik.data.Student;

import java.util.*;

public class CustomStudentAndGroupProvider extends StudentAndGroupProvider{

    public CustomStudentAndGroupProvider() {
        super(0, 18, null);
    }

    @Override
    public void generate() {
        studentList = new ArrayList<>();
        groupsBySize = new TreeMap<>();
        groupsBySize.put(1, new ArrayList<>());
        groupsBySize.put(2, new ArrayList<>());
        groupsBySize.put(3, new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            var group = new Group(new ArrayList<>());
            groupsBySize.get(3).add(group);
            for (int j = 3 * i; j < 3 * i + 3; j++) {
                group.students().add(new Student("Student " + j, "tu" + j + "id"));
            }
        }
        for (int i = 0; i < 3; i++) {
            var group = new Group(new ArrayList<>());
            groupsBySize.get(2).add(group);
            for (int j = 9 + 2 * i; j < 9 + 2 * i + 2; j++) {
                group.students().add(new Student("Student " + j, "tu" + j + "id"));
            }
        }
        for (int i = 0; i < 3; i++) {
            var group = new Group(new ArrayList<>());
            groupsBySize.get(1).add(group);
                group.students().add(new Student("Student " + i + 15, "tu" + i + 15 + "id"));
        }
    }

}
