package ch.joelniklaus.indoloc.statistics;

import java.text.DecimalFormat;

/**
 * Data object containing the train time and test time of a classifier.
 * <p>
 * Created by joelniklaus on 26.11.16.
 */

public class PerformanceRating extends Rating {
    private double accuracy;
    private double meanTrainTime;
    private double meanTestTime;

    public PerformanceRating(String name, double accuracy, double meanTestTime, double meanTrainTime) {
        this.name = name;
        this.accuracy = accuracy;
        this.meanTestTime = meanTestTime;
        this.meanTrainTime = meanTrainTime;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getMeanTrainTime() {
        return meanTrainTime;
    }

    public void setMeanTrainTime(double meanTrainTime) {
        this.meanTrainTime = meanTrainTime;
    }

    public double getMeanTestTime() {
        return meanTestTime;
    }

    public void setMeanTestTime(double meanTestTime) {
        this.meanTestTime = meanTestTime;
    }

    @Override
    public String toString() {
        return "\n" + fixedLengthString(name, 25)
                + fixedLengthString("Accuracy: " + new DecimalFormat("#.##").format(getAccuracy()) + " %", 25)
                + fixedLengthString("Test Time: " + meanTestTime + " µs", 25)
                + fixedLengthString("Train Time: " + meanTrainTime + " µs", 25);
    }
}
