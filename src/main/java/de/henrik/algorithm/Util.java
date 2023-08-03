package de.henrik.algorithm;

import de.henrik.Main;
import de.henrik.data.Application;
import de.henrik.data.IntegerTupel;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;

import java.util.*;

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

    public static List<Application> multiObjectiveKnapsack(List<Application> applications, int maxSize) {
        int n = applications.size();
        //FIRST -> SIZE; SECOND -> PRIORITY
        applications.sort(Comparator.comparingInt(Application::size));
        IntegerTupel[][] dp = new IntegerTupel[n + 1][maxSize + 1];
        HashMap<IntegerTupel, List<Application>> selectedApplications = new HashMap<>();
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= maxSize; j++) {
                dp[i][j] = new IntegerTupel(0, 0);
            }
        }

        Comparator<IntegerTupel> comparator = (o1, o2) -> {
            if (!Objects.equals(o1.first(), o2.first())) {
                return o1.first() - o2.first();
            } else {
                return o2.second() - o1.second();
            }
        };

        for (int i = 1; i <= n; i++) {
            Application app = applications.get(i - 1);

            for (int j = 1; j <= maxSize; j++) {
                if (app.size() > j) {
                    //App passt nicht rein, also Wert von davor übernehmen falls wir einen haben
                    dp[i][j] = dp[i - 1][j];
                    selectedApplications.put(new IntegerTupel(i, j), new ArrayList<>(selectedApplications.getOrDefault(new IntegerTupel(i - 1, j), new ArrayList<>())));
                } else {
                    //App passt rein, also gucken ob sie besser ist als was wir davor hatten
                    var newTupel = new IntegerTupel(dp[i - 1][j - app.size()].first() + app.size(), dp[i - 1][j - app.size()].second() + app.priority());
                    if (comparator.compare(dp[i - 1][j], newTupel) > 0) {
                        //Schlechter
                        dp[i][j] = dp[i - 1][j];
                        selectedApplications.put(new IntegerTupel(i, j), new ArrayList<>(selectedApplications.getOrDefault(new IntegerTupel(i - 1, j), new ArrayList<>())));
                    } else {
                        //Besser
                        dp[i][j] = newTupel;
                        selectedApplications.put(new IntegerTupel(i, j), new ArrayList<>(selectedApplications.getOrDefault(new IntegerTupel(i - 1, j - app.size()), new ArrayList<>())));
                        selectedApplications.get(new IntegerTupel(i, j)).add(app);
                    }

                }
            }
        }
        return selectedApplications.get(new IntegerTupel(n, maxSize));
    }
}
