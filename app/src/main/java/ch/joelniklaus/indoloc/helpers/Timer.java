package ch.joelniklaus.indoloc.helpers;

/**
 * A basic timer to measure the performance of certain processes.
 * <p>
 * Created by joelniklaus on 20.11.16.
 */
public class Timer {

    private long startTime;

    public Timer() {
        startTime = System.nanoTime();
    }

    public long timeElapsed() {
        return (System.nanoTime() - startTime) / 1000000;
    }

    public long timeElapsedMilliS() {
        return (System.nanoTime() - startTime) / 1000000;
    }

    public long timeElapsedMicroS() {
        return (System.nanoTime() - startTime) / 1000;
    }

    public long timeElapsedNanoS() {
        return (System.nanoTime() - startTime);
    }

    public void reset() {
        startTime = System.nanoTime();
    }
}
