package de.henrik.algorithm;

import de.henrik.generator.Provider;

public abstract class Algorithm {
    private final long seed;

    protected Algorithm(long seed) {
        this.seed = seed;
    }

    abstract void startAlgorithm(Provider provider);

}
