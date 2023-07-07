package de.henrik.generator;

import de.henrik.data.Group;
import de.henrik.data.Student;

import java.util.*;

public class StudentAndGroupProvider {
    public final long seed;
    private final int studentCount;
    private final Map<Integer, Integer> groupSizeToNumberOfStudents;

    private List<Student> studentList;
    private Map<Integer, List<Group>> groupsBySize;

    public StudentAndGroupProvider(long seed, int studentCount, Map<Integer, Integer> groupSizeToNumberOfStudents) {
        this.seed = seed;
        this.studentCount = studentCount;
        this.groupSizeToNumberOfStudents = groupSizeToNumberOfStudents;
    }

    public List<Group> getAllGroups() {
        if (groupsBySize == null) {
            throw new RuntimeException("Call generator first!");
        }
        return groupsBySize.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
    }

    public Map<Integer, List<Group>> getGroupsBySize() {
        return groupsBySize;
    }

    public List<Student> getStudentList() {
        if (studentList == null) {
            throw new RuntimeException("Call generator first!");
        }
        return studentList;
    }

    public void generate() {
        studentList = new ArrayList<>();
        groupsBySize = new TreeMap<>();
        Random random = new Random(seed);
        for (int i = 0; i < studentCount; i++) {
            studentList.add(new Student("Student " + i, "tu" + i + "id"));
        }
        for (int groupSize : groupSizeToNumberOfStudents.keySet()) {
            int generatedGroups = 0;
            groupsBySize.put(groupSize, new ArrayList<>());
            while (generatedGroups < groupSizeToNumberOfStudents.get(groupSize)) {
                List<Student> tempGroupList = new ArrayList<>();
                List<Integer> selectedIndices = new ArrayList<>(); // Keep track of selected indices
                for (int j = 0; j < groupSize; j++) {
                    int nextStudentIndex;
                    do {
                        nextStudentIndex = random.nextInt(studentList.size());
                    } while (selectedIndices.contains(nextStudentIndex)); // Ensure uniqueness
                    selectedIndices.add(nextStudentIndex);
                    Student student = studentList.get(nextStudentIndex);
                    tempGroupList.add(student);
                }
                if (groupsBySize.get(groupSize).stream().anyMatch(group -> new HashSet<>(group.students()).containsAll(tempGroupList))) {
                    continue;
                }
                groupsBySize.get(groupSize).add(new Group(tempGroupList));
                generatedGroups++;
            }
        }
    }
}