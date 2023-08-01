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
        super(0, new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), 5);
    }

    @Override
    public void generate() {
        courseList = new ArrayList<>();
        topicList = new ArrayList<>();

        var c = new Course ("Course 0");
        courseList.add(c);
        topicList.add(new Topic("Topic 0", c, new IntegerTupel(3,5), 1));
        topicList.add(new Topic("Topic 1", c, new IntegerTupel(3,5), 1));
        topicList.add(new Topic("Topic 2", c, new IntegerTupel(3,5), 1));
        topicList.add(new Topic("Topic 3", c, new IntegerTupel(3,5), 1));
    }
}
