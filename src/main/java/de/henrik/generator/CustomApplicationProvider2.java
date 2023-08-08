package de.henrik.generator;

import de.henrik.data.Application;
import de.henrik.data.Group;
import de.henrik.data.Topic;

import java.util.List;
import java.util.Map;

public class CustomApplicationProvider2 extends ApplicationsProvider {
    List<Group> groups;
    List<Topic> topics;

    public CustomApplicationProvider2() {
        super(0, null, false);
    }

    @Override
    public void generate(Map<Integer, List<Group>> groupsBySize, Map<Integer, List<Topic>> topicByAtMinSize) {
        groups = groupsBySize.get(1);
        topics = topicByAtMinSize.get(1);

        newApp(0, 2, 2).acceptApplication();
        newApp(0, 0, 1);
        newApp(1, 0, 2).acceptApplication();
        newApp(1, 1, 1);
        newApp(2, 1, 2).acceptApplication();
        newApp(2, 2, 1);

        applicationsList.addAll(applicationsHashMap.getApplicationList());
    }

    private Application newApp(int group, int topic, int prio) {
        var app = new Application(groups.get(group), topics.get(topic), 1, prio);
        applicationsHashMap.add(app);
        groups.get(group).applications().add(app);
        return app;
    }
}
