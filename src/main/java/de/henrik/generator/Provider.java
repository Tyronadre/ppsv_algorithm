package de.henrik.generator;

import de.henrik.data.Group;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Provider {
    long seed = 0L;
    public CourseAndTopicProvider courseAndTopicProvider;
    public StudentAndGroupProvider studentAndGroupProvider;
    public ApplicationsProvider applicationsProvider;

    public void fillGraph(Graph graph) {
        System.out.println("\n<--- COURSE AND TOPICS --->\n");
        courseAndTopicProvider = new CourseAndTopicProvider(seed, new IntegerTupel(10, 12), new IntegerTupel(3, 6), new IntegerTupel(4, 6), new IntegerTupel(4, 5), new IntegerTupel(3, 5), new IntegerTupel(2, 3), 5);
        courseAndTopicProvider.generate();
        int i = 0;
        for (Topic topic1 : courseAndTopicProvider.getTopicList()) {
            graph.addNode(topic1.toString());
            Node node = graph.getNode(topic1.toString());
            node.setAttribute("ui.label", topic1.name());
            node.setAttribute("data", topic1);
            node.setAttribute("ui.style", "fill-color: rgb(0, 100, 255);");
            node.setAttribute("xy", -100, i);
            node.setAttribute("ui.class", "topic");
            i += 10;
        }

        System.out.println("\n<--- STUDENT AND GROUPS --->\n");
        studentAndGroupProvider = new StudentAndGroupProvider(seed, 100, Map.of(1, 50, 2, 10, 3, 5));
        studentAndGroupProvider.generate();
        i = 0;
        for (Map.Entry<Integer, List<Group>> entry : studentAndGroupProvider.getGroupsBySize().entrySet()) {
            Integer key = entry.getKey();
            List<Group> value = entry.getValue();
            for (Group group : value) {
                graph.addNode(group.toString());
                Node node = graph.getNode(group.toString());
                node.setAttribute("ui.label", group.toString());
                node.setAttribute("data", group);
                node.setAttribute("ui.style", "fill-color: rgb(0, 255, 100);");
                node.setAttribute("xy", 100, i);
                node.setAttribute("ui.class", "group");
                i += 10;
            }
        }

        System.out.println("\n<--- APPLICATIONS --->\n");
        Map<Integer, Map<Integer, Integer>> applicationDistribution = new HashMap<>();
        Map<Integer, Integer> collectionSize1 = Map.of(1, 200, 2, 20, 3, 5);
        Map<Integer, Integer> collectionSize2 = Map.of(1, 20, 2, 5);
        Map<Integer, Integer> collectionSize3 = Map.of(1, 5);
        applicationDistribution.put(1, collectionSize1);
        applicationDistribution.put(2, collectionSize2);
        applicationDistribution.put(3, collectionSize3);
        applicationsProvider = new ApplicationsProvider(seed, applicationDistribution);
        HashMap<Integer, List<Topic>> topicBySize = new HashMap<>();
        for (Topic topic : courseAndTopicProvider.getTopicList()) {
            if (!topicBySize.containsKey(topic.participants().second())) {
                topicBySize.put(topic.participants().second(), new ArrayList<>());
            }
            topicBySize.get(topic.participants().second()).add(topic);
        }
        for (int size : topicBySize.keySet()) {
            for (int j = size; j > 0; j--) {
                topicBySize.get(j).addAll(topicBySize.get(size));
            }
        }


        applicationsProvider.generate(studentAndGroupProvider.getGroupsBySize(), topicBySize);

        int startRed = 255;
        int startGreen = 0;
        int startBlue = 0;
        int endRed = 0;
        int endGreen = 255;
        int endBlue = 255;

        applicationsProvider.getApplicationList().forEach(application -> {
            graph.addEdge(application.toString(), application.topic().toString(), application.group().toString(), false);
            Edge edge = graph.getEdge(application.toString());
            edge.setAttribute("data", application);


            double logProportion = 1 - Math.log10(application.priority()) / Math.log10(10);

            int red = (int) Math.round(startRed + (endRed - startRed) * logProportion);
            int green = (int) Math.round(startGreen + (endGreen - startGreen) * logProportion);
            int blue = (int) Math.round((application.collectionID() * 255) / 3.0);


            edge.setAttribute("ui.style", "fill-color: rgb(" + red + ", " + green + ", " + blue + ");");
        });

        System.out.println("\n <--- GEN FINISHED ---> \n");
//        courseAndTopicProvider.getTopicList().forEach(System.out::println);
//        studentAndGroupProvider.getGroupsBySize().forEach((size, groupList) -> groupList.forEach(System.out::println));
//        applicationsProvider.getApplicationList().forEach(System.out::println);
    }
}
