package de.henrik.algorithm;

import de.henrik.data.Application;
import de.henrik.data.Group;
import de.henrik.data.Tupel;
import de.henrik.generator.Provider;

import java.util.ArrayList;
import java.util.List;

public class CheckErrors {
    public static void check(Provider provider) {
        int errorCount = 0;
        var acceptedApplications = new ArrayList<Application>();
        for (var topic : provider.courseAndTopicProvider.getTopicList()) {
            for (var slot : topic.slots()) {
                if (!slot.validSlot() && slot.participants() > 0) {
                    System.err.println("Error in Slot " + slot);
                    errorCount++;
                }
                acceptedApplications.addAll(slot.acceptedApplications());
            }
        }
        List<Tupel<Group, Integer>> groupList = new ArrayList<>();
        for (var application : acceptedApplications) {
            if (groupList.contains(application.getGroupAndCollectionKey())) {
                System.err.println("Error: Group " + application.group() + " has multiple applications with the same collection key " + application.getGroupAndCollectionKey());
                errorCount++;
            } else {
                groupList.add(application.getGroupAndCollectionKey());
            }
        }
        if (errorCount == 0) {
            System.out.println("No errors found");
        } else {
            System.out.println("Found " + errorCount + " errors");
        }
    }
}
