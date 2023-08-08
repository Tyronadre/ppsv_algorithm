package de.henrik.generator;

import de.henrik.data.Application;
import de.henrik.data.Group;
import de.henrik.data.Student;

import java.util.*;

public class StudentAndGroupProvider {
    public final long seed;
    protected final int studentCount;
    private final Map<Integer, Integer> groupSizeToNumberOfStudents;

    protected List<Student> studentList;
    protected Map<Integer, List<Group>> groupsBySize;

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
        if (groupsBySize == null) {
            throw new RuntimeException("Call generator first!");
        }
        return groupsBySize;
    }

    public List<Student> getStudentList() {
        if (studentList == null) {
            throw new RuntimeException("Call generator first!");
        }
        return studentList;
    }

    public void generate() {
        System.out.println("Generating students and groups:");
        System.out.println("Student count: " + studentCount);
        groupSizeToNumberOfStudents.forEach((groupSize, numberOfGroups) -> System.out.println("Group size: " + groupSize + " - Number of groups: " + numberOfGroups));
        studentList = new ArrayList<>();
        groupsBySize = new TreeMap<>();
        Random random = new Random(seed);
        for (int i = 0; i < studentCount; i++) {
            studentList.add(new Student("Student " + i, "tu" + i + "id"));
        }
        for (int groupSize : groupSizeToNumberOfStudents.keySet()) {
            int generatedGroups = 0;
            groupsBySize.put(groupSize, new ArrayList<>());
            int groupsToGenerate = groupSizeToNumberOfStudents.get(groupSize);
            while (generatedGroups < groupsToGenerate) {
                System.out.print("Generating Group: " + generatedGroups + "/" + groupsToGenerate + " of size " + groupSize + "\r");
                List<Student> tempGroupList;
                if (groupSize > 1) {
                    tempGroupList = new ArrayList<>();
                    List<Integer> selectedIndices = new ArrayList<>();
                    for (int j = 0; j < groupSize; j++) {
                        int nextStudentIndex;
                        do {
                            nextStudentIndex = random.nextInt(studentList.size());
                        } while (selectedIndices.contains(nextStudentIndex));
                        selectedIndices.add(nextStudentIndex);
                        Student student = studentList.get(nextStudentIndex);
                        tempGroupList.add(student);
                    }

                } else {
                    tempGroupList = Collections.singletonList(studentList.get(random.nextInt(studentList.size())));
                }

                if (groupsBySize.get(groupSize).stream().anyMatch(group -> new HashSet<>(group.students()).containsAll(tempGroupList))) {
                    continue;
                }
                groupsBySize.get(groupSize).add(new Group(tempGroupList));
                generatedGroups++;
            }
        }
        System.out.println("Done!");
    }
}