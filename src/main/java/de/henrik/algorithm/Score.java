package de.henrik.algorithm;

import de.henrik.data.*;
import de.henrik.generator.Provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class Score {
    public static void score(Provider provider) {
        int score = 0;
        HashMap<Integer, IntegerTupel> acceptedApplicationsPrioMap = new HashMap<>();

        var acceptedApplicationsByKey = new TreeMap<Tupel<Group, Integer>, Application>(Comparator.comparing(Tupel::hashCode));
        for (var topic : provider.courseAndTopicProvider.getTopicList())
            for (var acceptedApplicationsTopic : topic.acceptedApplications())
                if (acceptedApplicationsByKey.containsKey(acceptedApplicationsTopic.getGroupAndCollectionKey()))
                    System.err.println("Error: Group " + acceptedApplicationsTopic.group() + " has multiple applications with the same collection key " + acceptedApplicationsTopic.getGroupAndCollectionKey());
                else
                    acceptedApplicationsByKey.put(acceptedApplicationsTopic.getGroupAndCollectionKey(), acceptedApplicationsTopic);
        var applicationsByKey = new TreeMap<Tupel<Group, Integer>, Application>(Comparator.comparing(Tupel::hashCode));
        for (var application : provider.applicationsProvider.getApplicationList())
            if (!applicationsByKey.containsKey(application.getGroupAndCollectionKey()))
                applicationsByKey.put(application.getGroupAndCollectionKey(), application);

        System.out.println("Accepted Applications: " + acceptedApplicationsByKey.size());
        var openApplications = new ArrayList<Application>();

        for (var uniqueApplication : applicationsByKey.values()) {
            if (acceptedApplicationsByKey.containsKey(uniqueApplication.getGroupAndCollectionKey())) {
                var app = acceptedApplicationsByKey.get(uniqueApplication.getGroupAndCollectionKey());
                score += app.size() * (10 - app.priority());
                acceptedApplicationsPrioMap.computeIfPresent(app.priority(), (k, v) -> new IntegerTupel(app.size() + v.first(), v.second() + 1));
                acceptedApplicationsPrioMap.computeIfAbsent(app.priority(), k -> new IntegerTupel(app.size(), 1));
            } else {
                score -= 10 - uniqueApplication.priority();
                acceptedApplicationsPrioMap.computeIfPresent(0, (k, v) -> new IntegerTupel(uniqueApplication.size() + v.first(), v.second() + 1));
                acceptedApplicationsPrioMap.computeIfAbsent(0, k -> new IntegerTupel(uniqueApplication.size(), 1));
                openApplications.add(uniqueApplication);
            }
        }

        var openSlots = new ArrayList<Tupel<Topic,Slot>>();
        for (var topic : provider.courseAndTopicProvider.getTopicList())
            for (var slot : topic.slots())
                if (slot.spaceLeft() > 0)
                    openSlots.add(new Tupel<>(topic, slot));

        System.out.println(" // --- Score --- // \n (Prio 0 -> not accepted)");
        System.out.println("Score: " + score);
        acceptedApplicationsPrioMap.forEach((k, v) -> System.out.println("Prio " + k + " (Students/Groups): " + v.first() + "/" + v.second()));
        System.out.println(" // --- Open Applications --- // ");
        openApplications.forEach(a -> System.out.println(a.getGroupAndCollectionKey() + ": Apps: " + a.group().applications().toString()));
        System.out.println(" // --- Open Slots --- // ");
        openSlots.forEach(t -> System.out.println(t.first().name() + ": Slot " + t.second().ID() + "; " + t.second().spaceLeft() + "; minSize: " + t.second().slotSize().first()));

    }
}
