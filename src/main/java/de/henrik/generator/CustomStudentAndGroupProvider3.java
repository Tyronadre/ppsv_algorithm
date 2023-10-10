package de.henrik.generator;

import de.henrik.data.Group;
import de.henrik.data.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class CustomStudentAndGroupProvider3 extends StudentAndGroupProvider {
    public CustomStudentAndGroupProvider3() {
        super(0, 3, null);
    }

    @Override
    public void generate() {
        groupsBySize = new TreeMap<>();
        groupsBySize.put(1, new ArrayList<>());
        studentList = new ArrayList<>();
        var s1 = new Student("Filip", "Filip");
        var s2 = new Student("Andrea", "Andrea");
        var s3 = new Student("Henrik", "Henrik");
        var s4 = new Student("Mike", "Mike");
        var s5 = new Student("Lorena", "Lorena");
        var s6 = new Student("Arsenia", "Arsenia");
        var s7 = new Student("Stefan", "Stefan");
        var s8 = new Student("Jellena", "Jellena");

        var group = new Group(new ArrayList<>());
        groupsBySize.get(1).add(group);
        group.students().add(s1);
        group.students().add(s2);
        groupsBySize.get(1).add(new Group(new ArrayList<>(Collections.singleton(s3))));
        groupsBySize.get(1).add(new Group(new ArrayList<>(Collections.singleton(s4))));
        groupsBySize.get(1).add(new Group(new ArrayList<>(Collections.singleton(s5))));
        groupsBySize.get(1).add(new Group(new ArrayList<>(Collections.singleton(s6))));
        groupsBySize.get(1).add(new Group(new ArrayList<>(Collections.singleton(s7))));
        groupsBySize.get(1).add(new Group(new ArrayList<>(Collections.singleton(s8))));

    }
}
