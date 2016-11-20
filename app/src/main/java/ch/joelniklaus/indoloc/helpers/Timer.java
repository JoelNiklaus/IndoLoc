package ch.joelniklaus.indoloc.helpers;

/**
 * Created by joelniklaus on 20.11.16.
 */

public class Timer {

    private long startTime;

    public Timer() {
        startTime = System.nanoTime();
    }

    public long timeElapsed() {
        return (System.nanoTime() - startTime)/1000000;
    }

    public void reset() {
        startTime = System.nanoTime();
    }
}
