package de.henrik.generator;

import de.henrik.data.Application;
import de.henrik.data.Group;
import de.henrik.data.Topic;
import scala.App;

import java.util.List;
import java.util.Map;

public class CustomApplicationsProvider extends ApplicationsProvider {
    List<Group> groups;
    List<Topic> topics;



    public CustomApplicationsProvider() {
        super(0, null);
    }

    @Override
    public void generate(Map<Integer, List<Group>> groupsBySize, Map<Integer, List<Topic>> topicByAtMinSize) {
        groups = groupsBySize.get(1);
        topics = topicByAtMinSize.get(1);

        newApp(0, 0,1 );
        newApp(0, 1,2 );
        newApp(0, 2,2 );
        newApp(0, 3,3 );
        newApp(1, 0, 1);
        newApp(1, 3, 2);
        newApp(2, 0, 2);
        newApp(2, 1, 1);
        newApp(2, 2, 3);

        groups = groupsBySize.get(2);
        newApp(0, 0, 1);
        newApp(0, 1, 2);
        newApp(1, 0, 3);
        newApp(1, 1, 2);
        newApp(1, 2, 1);
        newApp(2, 0, 1);
        newApp(2, 1, 2);
        newApp(2, 3, 3);

        groups = groupsBySize.get(3);
        newApp(0, 0, 1);
        newApp(0, 1, 2);
        newApp(1, 0, 1);
        newApp(1, 1, 2);
        newApp(2, 0, 1);
        newApp(2,3,2);
        newApp(2, 1, 3);


    }

    private void newApp(int group, int topic, int prio) {
        applicationsHashMap.add(new Application(groups.get(group), topics.get(topic), 1, prio));
    }
}
