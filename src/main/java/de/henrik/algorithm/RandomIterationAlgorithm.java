package de.henrik.algorithm;

import de.henrik.data.Application;
import de.henrik.data.Slot;
import de.henrik.data.Topic;
import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static de.henrik.Main.graph;
import static de.henrik.Main.provider;
import static de.henrik.algorithm.Util.highlightElement;


/**
 * This algorithm has one cycle.
 * It iterates over all slots of all topics and takes a random application from any group that has not been assigned so far.
 * If there are no applications left for a slot it goes to the next topic.
 * For a given group slot, this algorithm will assign a random combination of applications from all possible combinations for this slot.
 * If there are no combinations of applications that fit the slot, it will go to the next slot.
 */
public class RandomIterationAlgorithm extends Algorithm {


    public RandomIterationAlgorithm(long seed) {
        super(seed);
    }

    @Override
    @SuppressWarnings("Duplicates")
    void startAlgorithm() {

        var applicationHashMap = provider.applicationsProvider.getConcurrentApplicationHashMap();

        List<Application> acceptedApplications = new ArrayList<>();

        //we take a random application and take it
        for (Topic topic : provider.courseAndTopicProvider.getTopicList()) {
            //go through all slots for this topic
            //paint topic we are looking at
            highlightElement(graph.getNode(topic.name()));
            checkPause();

            if (!applicationHashMap.containsTopic(topic)) continue;
            for (Slot slot : topic.slots()) {
                //check if we have topics and if yes get them and paint them else go to the next slot
                if (applicationHashMap.getByTopicAndUpToSize(topic, slot.spaceLeft()) == null || applicationHashMap.getByTopicAndUpToSize(topic, slot.spaceLeft()).size() == 0) {
                    continue;
                }
                var possibleApplications = applicationHashMap.getByTopicAndUpToSize(topic, slot.spaceLeft());
                possibleApplications.forEach(app -> highlightElement(graph.getEdge(app.name())));
                checkPause();

                // we do single topics and group topics separately cause single topics are easier, and we have way more of them
                if (slot.spaceLeft() == 1) {
                    //Get a random application and accept it
                    var app = possibleApplications.get(random.nextInt(possibleApplications.size()));
                    acceptedApplications.add(app);
                    slot.acceptApplication(app);
                    applicationHashMap.removeAllWithSameKey(app);

                    //Repaint the edge
                    highlightElement(graph.getEdge(app.name()));
                    checkPause();
                } else {
                    // if we do a group assignment we need to do it in the following way:
                    // first we save how many applications are still fitting in the slot
                    // then we remove all applications that are too big
                    // then we look over all the applications in a combination that could completely fill the slot
                    // if that is not possible we take the biggest combination of applications that is possible
                    // if no combinations are possible or are big enough we go to the next slot
                    possibleApplications.sort(Comparator.comparingInt(Application::size));
                    int remainingSize = slot.spaceLeft();

                    //this can be optimized, and prob wont work for big stuff!!!
                    //get all possible combinations and remove all that are too big or too small
                    List<List<Application>> possibleCombinations = Util.generateDifferentPermutations(possibleApplications);
                    List<List<Application>> combinationsToRemove = new ArrayList<>();
                    for (var combination : possibleCombinations) {
                        var combParticipants = combination.stream().mapToInt(Application::size).sum();
                        if (combParticipants > remainingSize || combParticipants < slot.slotSize().first() + slot.participants()) {
                            combinationsToRemove.add(combination);
                        }
                    }
                    possibleCombinations.removeAll(combinationsToRemove);
                    // if we have no possible combinations we go to the next slot
                    if (possibleCombinations.size() == 0) {
                        continue;
                    }

                    // if we have possible combinations we take a random one
                    var applications = possibleCombinations.get(random.nextInt(possibleCombinations.size()));
                    for (Application app : applications) {
                        acceptedApplications.add(app);
                        slot.acceptApplication(app);
                        applicationHashMap.removeAllWithSameKey(app);
                        highlightElement(graph.getEdge(app.name()));
                        checkPause();
                    }
                }
                if (slow)
                    synchronized (this) {
                        try {
                            wait(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            }
            Util.repaintGraph();
        }
    }
}
