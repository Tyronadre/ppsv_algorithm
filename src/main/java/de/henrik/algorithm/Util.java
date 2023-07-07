package de.henrik.algorithm;

import de.henrik.data.Application;
import de.henrik.data.Topic;
import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static <T> List<List<T>> generateDifferentPermutations(List<T> input) {
        List<List<T>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), input);
        return result;
    }

    private static <T> void backtrack(List<List<T>> result, List<T> tempList, List<T> input) {
        if (tempList.size() > 0) {
            result.add(new ArrayList<>(tempList));
        }

        for (int i = 0; i < input.size(); i++) {
            tempList.add(input.get(i));
            List<T> nextInput = new ArrayList<>(input.subList(i + 1, input.size()));
            backtrack(result, tempList, nextInput);
            tempList.remove(tempList.size() - 1);
        }
    }

    public static void repaintGraph(Graph graph, Provider provider) {
        int startGreen = 0;
        int endGreen = 255;

        graph.edges().forEach(edge -> {
            Application application = edge.getAttribute("data", Application.class);

            double logProportion = 1 - Math.log10(application.priority()) / Math.log10(10);

            int red = 0;
            int green = (int) Math.round(startGreen + (endGreen - startGreen) * logProportion);
            int blue = (int) Math.round((application.collectionID() * 255) / 3.0);


            edge.setAttribute("ui.style", "fill-color: rgb(" + red + ", " + green + ", " + blue + ");");
        });
        graph.nodes().forEach(node -> {
            if (node.getAttribute("ui.class") == "topic") {
                node.setAttribute("ui.style", "fill-color: rgb(0, 100, 255);");
            } else {
                node.setAttribute("ui.style", "fill-color: rgb(0, 255, 100);");
            }
        });
        for (Topic topic : provider.courseAndTopicProvider.getTopicList()) {
            for (Application acceptedApplication : topic.acceptedApplications()) {
                graph.getEdge(acceptedApplication.toString()).setAttribute("ui.style", "fill-color: rgb(255, 0, 0);");
            }
        }
    }
}
