package de.henrik.algorithm;

import de.henrik.Main;
import de.henrik.data.Application;
import org.graphstream.graph.Element;
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
        System.out.println(result.size());
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

    public static void repaintGraph() {

        Main.graph.edges().forEach(edge -> {
            Application application = edge.getAttribute("data", Application.class);
            if (application.isAccepted()) {
                edge.setAttribute("ui.class", "accepted");
            } else {
                edge.setAttribute("ui.class", "");
            }
        });
        Main.graph.nodes().forEach(node -> {
            if (node.getAttribute("ui.class", String.class).contains("topic")) {
                node.setAttribute("ui.class", "topic");
            } else {
                node.setAttribute("ui.class", "group");

            }
        });
    }

    public static void addElementClass(Element element, String className) {
        String currentClasses = element.getAttribute("ui.class",String.class);
        if (currentClasses.length() == 0) {
            element.setAttribute("ui.class", className);
        } else if (!currentClasses.contains(className)) {
            String newClasses = currentClasses + ", " + className;
            element.setAttribute("ui.class", newClasses);
        }
    }

    public static void highlightElement(Element element) {
        if (element == null) {
            return;
        }
        addElementClass(element, "standout");
    }

    public static void highlightElement2(Element element) {
        if (element == null) {
            return;
        }
        addElementClass(element, "standout2");
    }

    public static void unhighlightElement(Element element) {
        if (element == null) {
            return;
        }
        String currentClasses = element.getAttribute("ui.class",String.class);
        if (currentClasses.contains("standout")) {
            String newClasses = currentClasses.replace("standout", "");
            element.setAttribute("ui.class", newClasses);
        }
    }

    public static void unhighlightElement2(Element element) {
        if (element == null) {
            return;
        }
        String currentClasses = element.getAttribute("ui.class",String.class);
        if (currentClasses.contains("standout2")) {
            String newClasses = currentClasses.replace("standout2", "");
            element.setAttribute("ui.class", newClasses);
        }
    }
}
