package de.henrik.algorithm;

import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;

import java.util.Random;


public abstract class Algorithm implements Runnable {

    boolean verbose = true;


    final Provider provider;
    final Graph graph;
    protected boolean oneStep = false;
    protected boolean pause = false;
    protected boolean slow = true;
    public static final int SLOW_TIME = 10;
    Random random;
    Long seed;

    private static boolean running = false;


    protected Algorithm(long seed, Provider provider, Graph graph) {
        random = new Random(seed);
        this.seed = seed;
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
            Util.repaintGraph(graph);
            System.out.println("Starting Algorithm with seed " + seed + " slow " + slow + " verbose " + verbose);
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
            //wait for gui
            try {
                wait(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
        if (slow)
            synchronized (this) {
                try {
                    wait(SLOW_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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

    public synchronized void setSlow(boolean visual) {
        this.slow = visual;
    }

    public boolean isPaused() {
        return pause;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
