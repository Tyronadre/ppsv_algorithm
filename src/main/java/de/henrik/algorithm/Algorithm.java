package de.henrik.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.henrik.Main.provider;


public abstract class Algorithm implements Runnable {

    protected static boolean verbose = false;
    protected static boolean oneStep = false;
    protected static boolean pause = false;
    protected static boolean slow = true;
    protected static int SLOW_TIME = 100;
    private static Algorithm algorithm = null;
    private List<Runnable> onFinishListener = new ArrayList<>();

    Random random;
    Long seed;

    protected Algorithm(long seed) {
        random = new Random(seed);
        this.seed = seed;
    }

    public static void step() {
        Algorithm.pause = false;
        Algorithm.oneStep = true;
        if (algorithm != null) {
            synchronized (algorithm) {
                algorithm.notify();
            }
        }
    }

    public static synchronized void setSlow(boolean slow) {
        Algorithm.slow = slow;
    }

    public static void setVerbose(boolean verbose) {
        Algorithm.verbose = verbose;
    }

    public static void setSpeed(int value) {
        Algorithm.SLOW_TIME = value;
    }


    @Override
    public void run() {
        if (algorithm == null) {
            Algorithm.algorithm = this;
            System.out.println("Clearing old Assignments");
            for (var topic : provider.courseAndTopicProvider.getTopicList()) {
                topic.clearApplications();
            }
            Util.repaintGraph();
            System.out.println("Starting Algorithm with seed " + seed + " slow " + slow + " verbose " + verbose);
            startAlgorithm();
            System.out.println("Algorithm finished");
            if (verbose) {
                Score.score(provider);
                CheckErrors.check(provider);
            }
            for (var listener : onFinishListener) {
                listener.run();
            }
            Algorithm.algorithm = null;
        }
    }

    public static boolean isRunning() {
        return algorithm != null;
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
            if (slow)
                try {
                    wait(SLOW_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    abstract void startAlgorithm();

    public static synchronized void pause() {
        Algorithm.pause = true;
    }

    public static synchronized void resume() {
        Algorithm.pause = false;
        synchronized (algorithm) {
            algorithm.notify();
        }
    }

    public void onFinish(Runnable r) {
        onFinishListener.add(r);
    }
}
