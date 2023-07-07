package de.henrik.algorithm;

import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;

import java.util.Random;

public abstract class Algorithm implements Runnable {
    final Provider provider;
    final Graph graph;
    protected boolean oneStep = false;
    protected boolean pause = false;
    Random random;

    private static boolean running = false;


    protected Algorithm(long seed, Provider provider, Graph graph) {
        random = new Random(seed);
        this.provider = provider;
        this.graph = graph;
    }


    @Override
    public void run() {
        if (!running) {
            running = true;
            System.out.println("Clearing old Assignments");
            for (var topic : provider.courseAndTopicProvider.getTopicList()) {
                topic.clearApplications();
            }
            System.out.println("Starting Algorithm");
            startAlgorithm();
            System.out.println("Algorithm finished");
            running = false;
        }
    }

    public static boolean isRunning() {
        return running;
    }

    void checkPause() {
        synchronized (this) {
            while (pause && !oneStep) {
                try {
                    wait(100);
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (oneStep) {
                oneStep = false;
                pause = true;
            }
        }
    }

    @SuppressWarnings("Duplicates")
    abstract void startAlgorithm();

    public synchronized void pause() {
        this.pause = true;
    }

    public synchronized void resume() {
        this.pause = false;
        notify();
    }


    public synchronized void oneStep() {
        oneStep = true;
        notify();
    }

    public boolean isPaused() {
        return pause;
    }
}
