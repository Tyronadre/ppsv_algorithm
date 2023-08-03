package de.henrik.algorithm;

import de.henrik.data.*;
import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;
import scala.Int;

import java.util.*;

import static de.henrik.Main.graph;
import static de.henrik.Main.provider;
import static de.henrik.algorithm.Util.highlightElement;


/**
 * This algorithm has multiple cyles.
 * In the first iteration this algorithm will assign each application to its first choice.
 * In the following iterations the algorithm will lower the priorities of all conflicting applications.
 */
public class GreedyCycleAlgorithm extends Algorithm {
    public GreedyCycleAlgorithm(long seed) {
        super(seed);
    }

    @Override
    void startAlgorithm() {
        var applicationsHashMap = provider.applicationsProvider.getConcurrentApplicationHashMap();

        //for this algorithm we need to sort the applications of each group by the priorities
        //we also want pointer what applications we have already looked at
        var applicationByKeySortedByPriorityHashMap = new TreeMap<Tupel<Group, Integer>, List<Application>>(Comparator.comparing(Tupel::hashCode));
        var applicationByKeySortedByPriorityHashMapPointer = new TreeMap<Tupel<Group, Integer>, Integer>(Comparator.comparing(Tupel::hashCode));
        for (var application : applicationsHashMap.getApplicationList()) {
            applicationByKeySortedByPriorityHashMap.computeIfAbsent(application.getGroupAndCollectionKey(), k -> new ArrayList<>()).add(application);
            applicationByKeySortedByPriorityHashMapPointer.computeIfAbsent(application.getGroupAndCollectionKey(), k -> 0);
        }
        for (var key : applicationByKeySortedByPriorityHashMap.keySet()) {
            applicationByKeySortedByPriorityHashMap.get(key).sort(Comparator.comparing(Application::priority));
        }

        //First iteration, just accept the first choice for every application
        for (var key : applicationByKeySortedByPriorityHashMap.keySet()) {
            var application = applicationByKeySortedByPriorityHashMap.get(key).get(0);
            applicationByKeySortedByPriorityHashMapPointer.put(key, 1);
            var edge = graph.getEdge(application.toString());
            application.topic().acceptApplication(application,0);
            highlightElement(edge);
            checkPause();
            synchronized (this) {
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Util.repaintGraph();

        // now we go through all applications do:
        // 1. if possible swap the application to another slot
        // 2. if not possible lower the priorities of all conflicting applications except one random one
        // we will find all possible applications of the slots and choose one of them at random
        // we will also keep track of the applications we have already looked at
        var brokenSlots = getBrokenSlots();
        for (var entry : brokenSlots.entrySet()) {
            var t = new ArrayList<>();
            for (var application : entry.getValue().acceptedApplications()) {
                t.addAll(applicationByKeySortedByPriorityHashMap.get(application.getGroupAndCollectionKey()));
            }

        }



    }

    private TreeMap<Topic, Slot> getBrokenSlots() {
        var result = new TreeMap<Topic, Slot>(Comparator.comparing(Topic::name));
        for (var topic : provider.courseAndTopicProvider.getTopicList()) {
            for (var slot : topic.slots()) {
                if (!slot.validSlot() && slot.participants() > 0) {
                    result.put(topic, slot);
                }
            }
        }
        return result;
    }
}
