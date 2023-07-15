package de.henrik.generator;

import de.henrik.data.Group;
import de.henrik.data.Student;

import java.util.*;

public class CustomStudentAndGroupProvider extends StudentAndGroupProvider{

    public CustomStudentAndGroupProvider() {
        super(0, 0, null);
    }

    @Override
    public void generate() {
        studentList = new ArrayList<>();
        groupsBySize = new TreeMap<>();
        groupsBySize.put(1, new ArrayList<>());
        for (int i = 0; i < 10; i++) {
            var student = new Student("Student " + i, "tu" + i + "id");
            studentList.add(student);
            groupsBySize.get(1).add(new Group(Collections.singletonList(student), new ArrayList<>()));
        }
    }

}
