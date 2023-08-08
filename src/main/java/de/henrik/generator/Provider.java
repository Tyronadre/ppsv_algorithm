package de.henrik.generator;

import de.henrik.algorithm.Util;
import de.henrik.data.Group;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Topic;
import org.graphstream.graph.Node;

import java.util.*;

import static de.henrik.Main.graph;

public class Provider {
    //    long seed = new Random().nextLong();
    public final int DATASET;
    long seed = 0;
    public CourseAndTopicProvider courseAndTopicProvider;
    public StudentAndGroupProvider studentAndGroupProvider;
    public ApplicationsProvider applicationsProvider;

    public Provider(int dataset) {
        DATASET = dataset;
    }

    public void fillGraph() {
        System.out.println("\n<--- COURSE AND TOPICS --->\n");
        courseAndTopicProvider = switch (DATASET) {
            case -2 -> new CustomCourseAndTopicProvider2();
            case -1 -> new CustomCourseAndTopicProvider();
            case 0 ->
                    new CourseAndTopicProvider(seed, new IntegerTupel(170, 180), new IntegerTupel(3, 6), new IntegerTupel(20, 30), new IntegerTupel(4, 5), new IntegerTupel(3, 5), new IntegerTupel(2, 3), 5);
            case 1 ->
                    new CourseAndTopicProvider(seed, new IntegerTupel(10, 10), new IntegerTupel(4, 4), new IntegerTupel(1, 1), new IntegerTupel(4, 4), new IntegerTupel(3, 3), new IntegerTupel(2, 2), 5);
            case 2 ->
                    new CourseAndTopicProvider(seed, new IntegerTupel(20, 20), new IntegerTupel(4, 4), new IntegerTupel(1, 1), new IntegerTupel(0, 0), new IntegerTupel(0, 0), new IntegerTupel(0, 0), 0);
            case 3 ->
                    new CourseAndTopicProvider(seed, new IntegerTupel(0, 0), new IntegerTupel(0, 0), new IntegerTupel(0, 0), new IntegerTupel(5, 5), new IntegerTupel(5, 5), new IntegerTupel(1, 1), 5);
            case 4 ->
                    new CourseAndTopicProvider(seed, new IntegerTupel(20000, 20000), new IntegerTupel(5, 5), new IntegerTupel(1, 1), new IntegerTupel(0, 0), new IntegerTupel(0, 0), new IntegerTupel(0, 0), 0);
            case 5 ->
                    new CourseAndTopicProvider(seed, new IntegerTupel(2, 2), new IntegerTupel(100000, 100000), new IntegerTupel(1, 1), new IntegerTupel(0, 0), new IntegerTupel(0, 0), new IntegerTupel(0, 0), 0);
            default -> throw new IllegalStateException("Unexpected value: " + DATASET);
        };


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
        studentAndGroupProvider = switch (DATASET) {
            case -2 -> new CustomStudentAndGroupProvider2();
            case -1 -> new CustomStudentAndGroupProvider();
            case 0 -> new StudentAndGroupProvider(seed, 1000, new TreeMap<>(Map.of(1, 900, 2, 5, 3, 5, 4, 10, 5, 20)));
            case 1 -> new StudentAndGroupProvider(seed, 50, new TreeMap<>(Map.of(1, 30, 2, 1, 3, 1, 4, 2, 5, 3)));
            case 2 -> new StudentAndGroupProvider(seed, 50, new TreeMap<>(Map.of(1, 50)));
            case 3 -> new StudentAndGroupProvider(seed, 50, new TreeMap<>(Map.of(1, 50)));
            case 4 ->
                    new StudentAndGroupProvider(seed, 50000, new TreeMap<>(Map.of(1, 9000, 2, 5, 3, 5, 4, 10, 5, 20)));
            case 5 -> new SingleStudentAndGroupProvider(100000);
            default -> throw new IllegalStateException("Unexpected value: " + DATASET);
        };
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
        int numberOfCollections;
        switch (DATASET) {
            case -2 -> {
                numberOfCollections = 1;
                applicationsProvider = new CustomApplicationProvider2();
            }
            case -1 -> {
                numberOfCollections = 1;
                applicationsProvider = new CustomApplicationProvider();
            }
            case 0 -> {
                numberOfCollections = 3;
                Map<Integer, Integer> collectionSize1 = new TreeMap<>(Map.of(1, 4500, 2, 7, 3, 7, 4, 30, 5, 100));
                Map<Integer, Integer> collectionSize2 = new TreeMap<>(Map.of(1, 200));
                Map<Integer, Integer> collectionSize3 = new TreeMap<>(Map.of(1, 20));
                applicationDistribution.put(1, collectionSize1);
                applicationDistribution.put(2, collectionSize2);
                applicationDistribution.put(3, collectionSize3);
                applicationsProvider = new ApplicationsProvider(seed, applicationDistribution, true);
            }
            case 1 -> {
                numberOfCollections = 2;
                Map<Integer, Integer> collectionSize1 = new TreeMap<>(Map.of(1, 200, 2, 1, 3, 1, 4, 4, 5, 10));
                Map<Integer, Integer> collectionSize2 = new TreeMap<>(Map.of(1, 20));
                applicationDistribution.put(1, collectionSize1);
                applicationDistribution.put(2, collectionSize2);
                applicationsProvider = new ApplicationsProvider(seed, applicationDistribution, true);
            }
            case 2 -> {
                numberOfCollections = 2;
                Map<Integer, Integer> collectionSize1 = new TreeMap<>(Map.of(1, 200));
                Map<Integer, Integer> collectionSize2 = new TreeMap<>(Map.of(1, 20));
                applicationDistribution.put(1, collectionSize1);
                applicationDistribution.put(2, collectionSize2);
                applicationsProvider = new ApplicationsProvider(seed, applicationDistribution, true);
            }
            case 3 -> {
                numberOfCollections = 2;
                Map<Integer, Integer> collectionSize1 = new TreeMap<>(Map.of(1, 200));
                Map<Integer, Integer> collectionSize2 = new TreeMap<>(Map.of(1, 20));
                applicationDistribution.put(1, collectionSize1);
                applicationDistribution.put(2, collectionSize2);
                applicationsProvider = new ApplicationsProvider(seed, applicationDistribution, true);
            }
            case 4 -> {
                numberOfCollections = 2;
                Map<Integer, Integer> collectionSize1 = new TreeMap<>(Map.of(1, 2000000));
                Map<Integer, Integer> collectionSize2 = new TreeMap<>(Map.of(1, 500000));
                applicationDistribution.put(1, collectionSize1);
                applicationDistribution.put(2, collectionSize2);
                applicationsProvider = new ApplicationsProvider(seed, applicationDistribution, false);
            }
            case 5 -> {
                numberOfCollections = 2;
                Map<Integer, Integer> collectionSize1 = new TreeMap<>(Map.of(1, 700000));
                Map<Integer, Integer> collectionSize2 = new TreeMap<>(Map.of(1, 10000));
                applicationDistribution.put(1, collectionSize1);
                applicationDistribution.put(2, collectionSize2);
                applicationsProvider = new ApplicationsProvider(seed, applicationDistribution, false);
            }
            default -> throw new IllegalStateException("Unexpected value: " + DATASET);
        }

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
            var edge = graph.addEdge(application.name(), application.topic().name(), application.group().toString(), false);
            edge.setAttribute("data", application);

            double logProportion = 1 - Math.log10(application.priority()) / Math.log10(10);

            int red = 0;
            int green = (int) Math.round(255 * logProportion);
            int blue = Math.round((float) (application.collectionID() * 255) / numberOfCollections);

            edge.setAttribute("ui.style", "fill-color: rgb(" + red + ", " + green + ", " + blue + "); z-index: -" + Math.abs(application.hashCode()) + ";");
        });

        Util.repaintGraph();

        System.out.println("\n <--- GEN FINISHED ---> \n");
//        courseAndTopicProvider.getTopicList().forEach(System.out::println);
//        studentAndGroupProvider.getGroupsBySize().forEach((size, groupList) -> groupList.forEach(System.out::println));
//        applicationsProvider.getApplicationList().forEach(System.out::println);
    }
}
