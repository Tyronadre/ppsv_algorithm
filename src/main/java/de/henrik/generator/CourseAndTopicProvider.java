package de.henrik.generator;

import de.henrik.data.Course;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CourseAndTopicProvider {
    private final long seed;
    private final IntegerTupel numberOfCourses;
    private final IntegerTupel numberOfTopicsPerCourse;
    private final IntegerTupel slotsOfTopic;

    private final IntegerTupel numberOfGroupCourses;
    private final IntegerTupel numberOfTopicsPerGroupCourse;
    private final IntegerTupel slotsOfGroupTopic;
    private final int maxSizeOfGroupTopic;

    private List<Course> courseList;
    private List<Topic> topicList;

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
        Random random = new Random(seed);
        courseList = new ArrayList<>();
        topicList = new ArrayList<>();

        //Normal Topics
        int courseCount = random.nextInt(numberOfCourses.first(), numberOfCourses.second());
        for (int courseI = 0; courseI < courseCount; courseI++) {
            Course course = new Course("Course " + courseI);
            courseList.add(course);
            int topicCount = random.nextInt(numberOfTopicsPerCourse.first(), numberOfTopicsPerCourse.second());
            for (int topicI = 0; topicI < topicCount; topicI++) {
                Topic topic = new Topic(course.name() + " Topic " + topicI, course, new IntegerTupel(1,1), random.nextInt(slotsOfTopic.first(), slotsOfTopic.second()));
                topicList.add(topic);
            }
        }

        //Group Topics
        int groupCourseCount = random.nextInt(numberOfGroupCourses.first(), numberOfGroupCourses.second());
        for (int courseI = 0; courseI < groupCourseCount; courseI++) {
            Course course = new Course("GroupCourse " + courseI);
            courseList.add(course);
            int groupTopicCount = random.nextInt(numberOfTopicsPerGroupCourse.first(), numberOfTopicsPerGroupCourse.second());
            for (int topicI = 0; topicI < groupTopicCount; topicI++) {
                int minSize = random.nextInt(1, maxSizeOfGroupTopic + 1);
                int maxSize = minSize == maxSizeOfGroupTopic ? minSize : random.nextInt(minSize, maxSizeOfGroupTopic+1);
                Topic topic = new Topic(course.name() + " GroupTopic " + topicI, course, new IntegerTupel(minSize,maxSize), random.nextInt(slotsOfGroupTopic.first(), slotsOfGroupTopic.second()));
                topicList.add(topic);
            }
        }
    }


}