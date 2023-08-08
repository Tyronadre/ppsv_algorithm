package de.henrik.generator;

import de.henrik.data.Course;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;

import java.util.ArrayList;

public class CustomCourseAndTopicProvider2 extends CourseAndTopicProvider{
    public CustomCourseAndTopicProvider2() {
        super(0, new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), 0);
    }

    @Override
    public void generate() {
        courseList = new ArrayList<>();
        topicList = new ArrayList<>();

        var c = new Course("Course 0");
        courseList.add(c);
        topicList.add(new Topic("Topic 0", c, new IntegerTupel(1,1), 1));
        topicList.add(new Topic("Topic 1", c, new IntegerTupel(1,1), 1));
        topicList.add(new Topic("Topic 2", c, new IntegerTupel(1,1), 1));
    }
}
