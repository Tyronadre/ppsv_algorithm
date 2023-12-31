package de.henrik.algorithm;

import de.henrik.data.Application;
import de.henrik.data.IntegerTupel;
import de.henrik.data.Slot;
import de.henrik.data.Topic;
import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;

import java.util.*;

import static de.henrik.Main.graph;
import static de.henrik.Main.provider;
import static de.henrik.algorithm.Util.*;


/**
 * This algorithm has one cycle.
 * It iterates over all topics and takes the application with the highest priority from any group that has not been assigned so far.
 * If there are no applications left for a topic it goes to the next topic.
 * For a given group slot, this algorithm will find the biggest combination of applications that still fits the slot with the smallest combined priority.
 * If there are no combinations of applications that fit the slot, it will go to the next slot.
 */
public class HighestPriorityAlgorithm extends Algorithm {
    public HighestPriorityAlgorithm(long seed) {
        super(seed);
    }



    @Override
    @SuppressWarnings("Duplicates")
    void startAlgorithm() {
        var applicationHashMap = provider.applicationsProvider.getConcurrentApplicationHashMap();

        //we take a random application and take it
        var counter = 0;
        float maxCounter = provider.courseAndTopicProvider.getTopicList().stream().mapToInt(topic -> topic.slots().size()).sum();
        for (Topic topic : provider.courseAndTopicProvider.getTopicList()) {
            //go through all slots for this topic
            //paint topic we are looking at
            if (slow) highlightElement(graph.getNode(topic.name()));
            checkPause();

            if (!applicationHashMap.containsTopic(topic)) continue;
            for (Slot slot : topic.slots()) {
                System.out.print("Progress " + String.format("%.2f", ((counter++ / maxCounter) * 100)) + "%           \r");
                //check if we have topics and if yes get them and paint them else go to the next slot
                if (applicationHashMap.getByTopicAndUpToSize(topic, slot.spaceLeft()) == null || applicationHashMap.getByTopicAndUpToSize(topic, slot.spaceLeft()).isEmpty()) {
                    continue;
                }
                var possibleApplications = applicationHashMap.getByTopicAndUpToSize(topic, slot.spaceLeft());
                //sort the applications by priority (they should be sorted but we do this for safety)
                possibleApplications.sort(Comparator.comparingInt(Application::priority));
                if (slow) possibleApplications.forEach(app -> highlightElement(graph.getEdge(app.name())));
                checkPause();

                // we do single topics and group topics separately cause single topics are easier, and we have way more of them
                if (slot.spaceLeft() == 1) {
                    //Get the application with the highest prio and accept it
                    var app = possibleApplications.get(0);
                    slot.acceptApplication(app);
                    applicationHashMap.removeAllWithSameKey(app);

                    //Repaint the edge
                    if (slow) highlightElement2(graph.getEdge(app.name()));
                } else {
                    var combination = multiObjectiveKnapsack(possibleApplications, slot.spaceLeft());
                    if (combination.stream().mapToInt(Application::size).sum() >= slot.slotSize().first() + slot.participants()) {
                        combination.forEach(app -> {
                            slot.acceptApplication(app);
                            applicationHashMap.removeAllWithSameKey(app);
                            if (slow) highlightElement2(graph.getEdge(app.name()));
                        });
                    }
                }
                checkPause();
            }
            if (slow) Util.repaintGraph();
        }
    }


}
