package ch.joelniklaus.indoloc.helpers;

import java.text.DecimalFormat;

/**
 * Created by joelniklaus on 26.11.16.
 */

public class ClassifierRating {
    private String name;
    private double meanTrainTime;
    private double meanTestTime;
    private double meanAccuracy;

    public ClassifierRating(String name, double meanAccuracy, double meanTestTime, double meanTrainTime) {
        this.name = name;
        this.meanAccuracy = meanAccuracy;
        this.meanTestTime = meanTestTime;
        this.meanTrainTime = meanTrainTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getMeanAccuracy() {
        return meanAccuracy;
    }

    public void setMeanAccuracy(double meanAccuracy) {
        this.meanAccuracy = meanAccuracy;
    }

    @Override
    public String toString() {
        return "\n" + fixedLengthString(name, 15) +
                fixedLengthString("Accuracy: " + new DecimalFormat("#.##").format(meanAccuracy) + " %", 25)
                + fixedLengthString("Test Time: " + meanTestTime + " µs", 25)
                + fixedLengthString("Train Time: " + meanTrainTime + " µs", 25);
    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }
}
