package de.henrik.generator;

import de.henrik.algorithm.Util;
import de.henrik.data.Group;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

public class Provider {
    long seed = new Random().nextLong();
    public CourseAndTopicProvider courseAndTopicProvider;
    public StudentAndGroupProvider studentAndGroupProvider;
    public ApplicationsProvider applicationsProvider;

    public void fillGraph(Graph graph) {
        System.out.println("\n<--- COURSE AND TOPICS --->\n");
        courseAndTopicProvider = new CourseAndTopicProvider(seed, new IntegerTupel(10, 12), new IntegerTupel(3, 6), new IntegerTupel(4, 6), new IntegerTupel(4, 5), new IntegerTupel(3, 5), new IntegerTupel(2, 3), 5);
        courseAndTopicProvider.generate();
        int i = 0;
        for (Topic topic1 : courseAndTopicProvider.getTopicList()) {
            graph.addNode(topic1.name());
            Node node = graph.getNode(topic1.name());
            node.setAttribute("ui.label", topic1.name() + " " + topic1.slotSize());
            node.setAttribute("data", topic1);
            node.setAttribute("xy", -100, i);
            node.setAttribute("ui.class", "topic");
            i += 10;
        }

        System.out.println("\n<--- STUDENT AND GROUPS --->\n");
        studentAndGroupProvider = new StudentAndGroupProvider(seed, 100, new TreeMap<>(Map.of(1, 50, 2, 10, 3, 5)));
        studentAndGroupProvider.generate();
        i = 0;
        for (Map.Entry<Integer, List<Group>> entry : studentAndGroupProvider.getGroupsBySize().entrySet()) {
            List<Group> value = entry.getValue();
            for (Group group : value) {
                graph.addNode(group.toString());
                Node node = graph.getNode(group.toString());
                node.setAttribute("ui.label", group.toString());
                node.setAttribute("data", group);
                node.setAttribute("xy", 100, i);
                node.setAttribute("ui.class", "group");
                i += 10;
            }
        }

        System.out.println("\n<--- APPLICATIONS --->\n");
        Map<Integer, Map<Integer, Integer>> applicationDistribution = new TreeMap<>();
        Map<Integer, Integer> collectionSize1 = new TreeMap<>(Map.of(1, 200, 2, 20, 3, 5));
        Map<Integer, Integer> collectionSize2 = new TreeMap<>(Map.of(1, 20, 2, 5));
        Map<Integer, Integer> collectionSize3 = new TreeMap<>(Map.of(1, 5));
        applicationDistribution.put(1, collectionSize1);
        applicationDistribution.put(2, collectionSize2);
        applicationDistribution.put(3, collectionSize3);
        applicationsProvider = new ApplicationsProvider(seed, applicationDistribution);

        var topicsByMaxSlotSize = courseAndTopicProvider.topicsByMaxSlotSize();
        // key 1 -> All topics that a group size 1 can apply to, etc.
        TreeMap<Integer, List<Topic>> topicsMapping = new TreeMap<>();
        for (int j = 1; j <= courseAndTopicProvider.getMaxSizeOfGroupTopic(); j++) {
            for (int k = j; k <= courseAndTopicProvider.getMaxSizeOfGroupTopic(); k++) {
                if (topicsByMaxSlotSize.get(k) == null) continue;
                topicsMapping.computeIfAbsent(j, key -> new ArrayList<>()).addAll(topicsByMaxSlotSize.get(k));
            }
        }


        applicationsProvider.generate(studentAndGroupProvider.getGroupsBySize(), topicsMapping);

        applicationsProvider.getApplicationList().forEach(application -> {
            System.out.println("Adding edge: " + application);
            graph.addEdge(application.toString(), application.topic().name(), application.group().toString(), false);
            Edge edge = graph.getEdge(application.toString());
            edge.setAttribute("data", application);
        });

        Util.repaintGraph(graph, this);

        System.out.println("\n <--- GEN FINISHED ---> \n");
        courseAndTopicProvider.getTopicList().forEach(System.out::println);
        studentAndGroupProvider.getGroupsBySize().forEach((size, groupList) -> groupList.forEach(System.out::println));
        applicationsProvider.getApplicationList().forEach(System.out::println);
    }
}
