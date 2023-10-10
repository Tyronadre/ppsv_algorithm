package de.henrik.generator;

import de.henrik.data.Course;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;

import java.util.ArrayList;

public class CustomCourseAndTopicProvider3 extends CourseAndTopicProvider{
    public CustomCourseAndTopicProvider3() {
        super(0, new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), new IntegerTupel(0,0), 2);
    }

    @Override
    public void generate() {
        courseList = new ArrayList<>();
        topicList = new ArrayList<>();

        var c = new Course("Apartment 1");
        var c1 = new Course("Apartment 2");
        courseList.add(c);
        courseList.add(c1);
        topicList.add(new Topic("Room 1", c, new IntegerTupel(2,2), 1));
        topicList.add(new Topic("Room 2", c, new IntegerTupel(2,2), 1));
        topicList.add(new Topic("Room 1", c1, new IntegerTupel(2,2), 1));
        topicList.add(new Topic("Room 2", c1, new IntegerTupel(2,2), 1));
    }
}
