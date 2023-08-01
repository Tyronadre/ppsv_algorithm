package de.henrik.test;

import de.henrik.data.IntegerTupel;

import java.util.*;

class Application {
    int priority;
    int size;

    public Application(int priority, int size) {
        this.priority = priority;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Application{" + size + "/" + priority + '}';
    }
}

public class KnapsackAlgorithm {
    public static List<Application> knapsack(List<Application> applications, int maxSize) {
        int n = applications.size();
        int[][] dp = new int[n + 1][maxSize + 1];
        //Damit wir knapsack benutzen können müssen wir ein bisschen tricksen und alle prios "Invertieren" damit prio 1 tatsächlich den höchsten integer wert hat.
        for (Application application : applications) {
            application.priority = 100 - application.priority;
        }

        for (int i = 1; i <= n; i++) {
            Application app = applications.get(i - 1);

            for (int j = 1; j <= maxSize; j++) {
                if (app.size > j) {
                    //App passt nicht mehr rein, also Wert von davor übernehmen
                    dp[i][j] = dp[i - 1][j];
                }
                if (app.size <= j) {
                    dp[i][j] = Math.max(
                            dp[i-1][j],
                            dp[i - 1][j - app.size] + app.priority);
                }
            }
        }

        // Find the minimum priority achieved and the corresponding applications
        List<Application> selectedApplications = new ArrayList<>();
        int j = maxSize;

        for (int i = n; i > 0; i--) {
            if (dp[i][j] != dp[i - 1][j]) {
                Application app = applications.get(i - 1);
                selectedApplications.add(app);
                j -= app.size;
            }
        }

        return selectedApplications;
    }


    public static List<Application> multiObjectiveKnapsack(List<Application> applications, int maxSize) {
        int n = applications.size();
        //FIRST -> SIZE; SECOND -> PRIORITY
        applications.sort(Comparator.comparingInt(o -> o.size));
        IntegerTupel[][] dp = new IntegerTupel[n + 1][maxSize + 1];
        HashMap<IntegerTupel, List<Application>> selectedApplications = new HashMap<>();
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= maxSize; j++) {
                dp[i][j] = new IntegerTupel(0, 0);
            }
        }
        //Damit wir knapsack benutzen können müssen wir ein bisschen tricksen und alle prios "Invertieren" damit prio 1 tatsächlich den höchsten integer wert hat.
        for (Application application : applications) {
            application.priority = 100 - application.priority;
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
                if (app.size > j) {
                    //App passt nicht rein, also Wert von davor übernehmen falls wir einen haben
                    dp[i][j] = dp[i - 1][j];
                    selectedApplications.put(new IntegerTupel(i,j), new ArrayList<>(selectedApplications.getOrDefault(new IntegerTupel(i-1,j), new ArrayList<>())));
                }
                else {
                    //App passt rein, also gucken ob sie besser ist als was wir davor hatten
                    var newTupel = new IntegerTupel(dp[i-1][j- app.size].first() + app.size, dp[i-1][j- app.size].second() + app.priority);
                    if (comparator.compare(dp[i-1][j],newTupel) > 0) {
                        dp[i][j] = dp[i-1][j];
                        selectedApplications.put(new IntegerTupel(i,j), new ArrayList<>(selectedApplications.getOrDefault(new IntegerTupel(i-1,j), new ArrayList<>())));
                    } else {
                        dp[i][j] = newTupel;
                        selectedApplications.put(new IntegerTupel(i,j), new ArrayList<>(selectedApplications.getOrDefault(new IntegerTupel(i-1,j- app.size), new ArrayList<>())));
                        selectedApplications.get(new IntegerTupel(i,j)).add(app);
                    }

                }
            }
        }

        for (IntegerTupel[] integerTupels : dp) {
            for (int k = 0; k < dp[0].length; k++) {
                System.out.print(integerTupels[k] + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < dp.length; i++) {
            for (int k = 0; k < dp[0].length; k++) {
                System.out.print(selectedApplications.get(new IntegerTupel(i,k)) + " ");
            }
            System.out.println();
        }

        return selectedApplications.get(new IntegerTupel(n,maxSize));
    }




    public static void main(String[] args) {
        List<Application> applications = new ArrayList<>();
        applications.add(new Application(3, 1));
        applications.add(new Application(3, 1));
        applications.add(new Application(100, 5));
        applications.add(new Application(2, 1));

        int minSize = 4;
        int maxSize = 5;

        List<Application> selectedApplications = knapsack(applications, maxSize);

        int minPriority = 0;
        for (Application app : selectedApplications) {
            minPriority += app.priority;
        }

        System.out.println("Minimum Priority: " + minPriority);
        System.out.println("Selected Applications:");
        for (Application app : selectedApplications) {
            System.out.println("Priority: " + app.priority + ", Size: " + app.size);
        }


        List<Application> selectedApplications2 = multiObjectiveKnapsack(applications, maxSize);
        System.out.println("Multi-Objective Knapsack:");
        selectedApplications2.forEach(System.out::println);



    }
}

