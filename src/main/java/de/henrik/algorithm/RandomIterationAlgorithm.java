package de.henrik.algorithm;

import de.henrik.generator.Provider;

public class RandomIterationAlgorithm extends Algorithm{
    protected RandomIterationAlgorithm(long seed) {
        super(seed);
    }

    @Override
    void startAlgorithm(Provider provider) {
        var applicationsList = provider.applicationsProvider.getApplicationList();
        var applicationsPerGroupSizePerCollection = provider.applicationsProvider.getApplicationsPerGroupSizePerCollection();
        var applicationsBySize = provider.applicationsProvider.getApplicationsBySize();

        var t = provider.courseAndTopicProvider.getTopicList();

    }
}
