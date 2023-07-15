package de.henrik.generator;

import de.henrik.data.Course;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;

import java.util.ArrayList;
import java.util.Random;

public class CustomCourseAndTopicProvider extends CourseAndTopicProvider{
    /**
     *
     */
    public CustomCourseAndTopicProvider() {
        super(0, new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), 0);
    }

    @Override
    public void generate() {
        courseList = new ArrayList<>();
        topicList = new ArrayList<>();

        //Normal Topics
        int courseCount = 5;
        int topicCount = 2;
        for (int courseI = 0; courseI < courseCount; courseI++) {
            Course course = new Course("Course " + courseI);
            courseList.add(course);
            for (int topicI = 0; topicI < topicCount; topicI++) {
                Topic topic = new Topic(course.name() + " Topic " + topicI, course, new IntegerTupel(1, 1), 1);
                topicList.add(topic);
            }
        }
    }
}
