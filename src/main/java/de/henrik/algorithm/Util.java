package de.henrik.algorithm;

import de.henrik.Main;
import de.henrik.data.Application;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Tupel;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;

import java.util.*;

import static de.henrik.Main.provider;

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
        String currentClasses = element.getAttribute("ui.class", String.class);
        if (currentClasses.length() == 0) {
            element.setAttribute("ui.class", className);
        } else if (!currentClasses.contains(className)) {
            String newClasses = currentClasses + ", " + className;
            element.setAttribute("ui.class", newClasses);
        }
    }

    public static void highlightElement(Element element) {
        if (element == null) {
            System.err.println("Didn't find element " + element);
            return;
        }
        addElementClass(element, "standout");
    }

    public static void highlightElement2(Element element) {
        if (element == null) {
            System.err.println("Didn't find element " + element);
            return;
        }
        addElementClass(element, "standout2");
    }

    public static void unhighlightElement(Element element) {
        if (element == null) {
            return;
        }
        String currentClasses = element.getAttribute("ui.class", String.class);
        if (currentClasses.contains("standout")) {
            String newClasses = currentClasses.replace("standout", "");
            element.setAttribute("ui.class", newClasses);
        }
    }

    public static void unhighlightElement2(Element element) {
        if (element == null) {
            return;
        }
        String currentClasses = element.getAttribute("ui.class", String.class);
        if (currentClasses.contains("standout2")) {
            String newClasses = currentClasses.replace("standout2", "");
            element.setAttribute("ui.class", newClasses);
        }
    }

    /**
     * Finds the best combination of applications with two objectives: size and priority
     *
     * @param applications List of applications
     * @param maxSize      Maximum size of the combination
     * @return List of applications that have the best combination of size and priority
     */
    public static List<Application> multiObjectiveKnapsack(List<Application> applications, int maxSize) {
        if (applications.isEmpty() || maxSize <= 0) {
            return new ArrayList<>();
        }

        return Objects.requireNonNull(multiObjectiveKnapsackWithResult(applications, maxSize)).first();
    }

    public static Tupel<List<Application>, KnapsackResult> multiObjectiveKnapsackWithResult(List<Application> applications, int maxSize) {
        if (applications.isEmpty() || maxSize <= 0) {
            return null;
        }

        applications.sort(Comparator.comparingInt(Application::size));

        KnapsackResult[][] dp = new KnapsackResult[applications.size() + 1][maxSize + 1];
        for (int i = 0; i <= applications.size(); i++) {
            for (int j = 0; j <= maxSize; j++) {
                dp[i][j] = new KnapsackResult(0, 0);
            }
        }

        for (int i = 1; i <= applications.size(); i++) {
            Application app = applications.get(i - 1);

            for (int j = 1; j <= maxSize; j++) {
                if (app.size() > j) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    KnapsackResult withoutApp = dp[i - 1][j];
                    KnapsackResult withApp = new KnapsackResult(dp[i - 1][j - app.size()].size + app.size(), dp[i - 1][j - app.size()].priority + app.priority());

                    dp[i][j] = (withoutApp.compareTo(withApp) > 0) ? withoutApp : withApp;
                }
            }
        }

        List<Application> selectedApplications = new ArrayList<>();
        int i = applications.size();
        int j = maxSize;

        while (i > 0 && j > 0) {
            if (!dp[i][j].equals(dp[i - 1][j])) {
                selectedApplications.add(applications.get(i - 1));
                j -= applications.get(i - 1).size();
            }
            i--;
        }

        return new Tupel<>(selectedApplications, dp[applications.size()][maxSize]);
    }

    record KnapsackResult(int size, int priority) implements Comparable<KnapsackResult> {
        @Override
        public int compareTo(KnapsackResult other) {
            if (this.size != other.size) {
                return this.size - other.size;
            } else {
                return other.priority - this.priority;
            }
        }
    }

    public static void clear() {
        provider.courseAndTopicProvider.clear();
        provider.applicationsProvider.clear();
        repaintGraph();
    }
}
