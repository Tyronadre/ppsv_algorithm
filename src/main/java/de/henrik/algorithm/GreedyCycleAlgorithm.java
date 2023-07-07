package de.henrik.algorithm;

import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;

/**
 * This algorithm has multiple cyles.
 * In the first iteration this algorithm will assign each application to its first choice.
 * In the following iterations the algorithm will lower the priorities of all conflicting applications.
 */
public class GreedyCycleAlgorithm extends Algorithm {
    protected GreedyCycleAlgorithm(long seed, Provider provider, Graph graph) {
        super(seed, provider, graph);
    }

    @Override
    void startAlgorithm() {

    }
}
