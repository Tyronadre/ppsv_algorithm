package de.henrik.generator;

import de.henrik.data.Course;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class CourseAndTopicProvider {
    private final long seed;
    private final IntegerTupel numberOfCourses;
    private final IntegerTupel numberOfTopicsPerCourse;
    private final IntegerTupel slotsOfTopic; //not used atm. we only have single slot single topics.

    private final IntegerTupel numberOfGroupCourses;
    private final IntegerTupel numberOfTopicsPerGroupCourse;
    private final IntegerTupel slotsOfGroupTopic;
    private final int maxSizeOfGroupTopic;

    protected List<Course> courseList;
    protected List<Topic> topicList;

    /**
     * Bounds are INCLUDING
     *
     * @param seed                         the seed
     * @param numberOfCourses
     * @param numberOfTopicsPerCourse
     * @param slotsOfTopic
     * @param numberOfGroupCourses
     * @param numberOfTopicsPerGroupCourse
     * @param slotsOfGroupTopic
     * @param maxSizeOfGroupTopic
     */
    public CourseAndTopicProvider(long seed, IntegerTupel numberOfCourses, IntegerTupel numberOfTopicsPerCourse, IntegerTupel slotsOfTopic, IntegerTupel numberOfGroupCourses, IntegerTupel numberOfTopicsPerGroupCourse, IntegerTupel slotsOfGroupTopic, int maxSizeOfGroupTopic) {
        this.seed = seed;
        this.numberOfCourses = numberOfCourses;
        this.numberOfTopicsPerCourse = numberOfTopicsPerCourse;
        this.slotsOfTopic = slotsOfTopic;
        this.numberOfGroupCourses = numberOfGroupCourses;
        this.numberOfTopicsPerGroupCourse = numberOfTopicsPerGroupCourse;
        this.slotsOfGroupTopic = slotsOfGroupTopic;
        this.maxSizeOfGroupTopic = maxSizeOfGroupTopic;
    }

    public int getMaxSizeOfGroupTopic() {
        return maxSizeOfGroupTopic == 0 ? 1 : maxSizeOfGroupTopic;
    }

    public List<Course> getCourseList() {
        if (courseList == null) {
            throw new RuntimeException("Call generator first!");
        }
        return new ArrayList<>(courseList);
    }

    public List<Topic> getTopicList() {
        if (topicList == null) {
            throw new RuntimeException("Call generator first!");
        }
        return new ArrayList<>(topicList);
    }

    public void generate() {
        System.out.println("Generating courses and topics:");
        System.out.println("Number of courses: " + numberOfCourses + " Number of topics per course: " + numberOfTopicsPerCourse + " Number of group courses: " + numberOfGroupCourses + " Number of topics per group course: " + numberOfTopicsPerGroupCourse + " Max size of group topic: " + maxSizeOfGroupTopic);
        Random random = new Random(seed);
        courseList = new ArrayList<>();
        topicList = new ArrayList<>();

        //Normal Topics
        int courseCount = random.nextInt(numberOfCourses.first(), numberOfCourses.second() + 1);
        for (int courseI = 0; courseI < courseCount; courseI++) {
            Course course = new Course("Course " + courseI);
            courseList.add(course);
            int topicCount = random.nextInt(numberOfTopicsPerCourse.first(), numberOfTopicsPerCourse.second() + 1);
            for (int topicI = 0; topicI < topicCount; topicI++) {
                Topic topic = new Topic(course.name() + " Topic " + topicI, course, new IntegerTupel(1, 1), 1);
                topicList.add(topic);
            }
        }

        //Group Topics
        int groupCourseCount = random.nextInt(numberOfGroupCourses.first(), numberOfGroupCourses.second() + 1);
        for (int courseI = 0; courseI < groupCourseCount; courseI++) {
            Course course = new Course("GroupCourse " + courseI);
            courseList.add(course);
            int groupTopicCount = random.nextInt(numberOfTopicsPerGroupCourse.first(), numberOfTopicsPerGroupCourse.second() + 1);
            for (int topicI = 0; topicI < groupTopicCount; topicI++) {
                int minSize = random.nextInt(1, maxSizeOfGroupTopic + 1);
                int maxSize = minSize == maxSizeOfGroupTopic ? minSize : random.nextInt(minSize, maxSizeOfGroupTopic + 1);
                Topic topic = new Topic(course.name() + " GroupTopic " + topicI, course, new IntegerTupel(minSize, maxSize), random.nextInt(slotsOfGroupTopic.first(), slotsOfGroupTopic.second() + 1));
                topicList.add(topic);
            }
        }
        System.out.println("Done!");
    }


    public TreeMap<Integer, ArrayList<Topic>> topicsByMaxSlotSize() {
        TreeMap<Integer, ArrayList<Topic>> result = new TreeMap<>();
        for (Topic topic : topicList)
            result.computeIfAbsent(topic.slotSize().second(), k -> new ArrayList<>()).add(topic);
        return result;
    }

    public void clear() {
        for (Topic topic : getTopicList()) {
            topic.clearApplications();
        }
    }
}