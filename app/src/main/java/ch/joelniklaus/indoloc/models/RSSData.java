package ch.joelniklaus.indoloc.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by joelniklaus on 11.11.16.
 */
public class RSSData implements Serializable {

    private ArrayList<Integer> values;

    private double mean;

    private ArrayList<Double> variances;

    public static RSSData createRSSDataTest(ArrayList<Integer> values) {
        RSSData rssData = new RSSData();
        rssData.setValues(values);
        double mean = mean(values);
        rssData.setMean(mean);
        rssData.setVariances(variances(mean, values));
        return rssData;
    }

    public RSSData() {

    }

    public RSSData(ArrayList<Integer> values) {
        this.values = computeRelativeRSSValues(values);
        this.mean = mean(values);
        this.variances = variances(mean, values);
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public void setValues(ArrayList<Integer> values) {
        this.values = values;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public ArrayList<Double> getVariances() {
        return variances;
    }

    public void setVariances(ArrayList<Double> variances) {
        this.variances = variances;
    }

    /**
     * Computes the RSS Value relative to the maximum value.
     */
    private ArrayList<Integer> computeRelativeRSSValues(ArrayList<Integer> values) {
        int max = Collections.max(values);
        for (int i = 0; i < values.size(); i++) {
            values.set(i, max - values.get(i));
        }
        return values;
    }

    private static ArrayList<Double> variances(double mean, ArrayList<Integer> values) {
        ArrayList<Double> variances = new ArrayList<>();
        for (int i = 0; i < values.size(); i++)
            variances.add(i, mean - values.get(i));
        return variances;
    }

    // Better use a `List`. It is more generic and it also receives an `ArrayList`.
    public static double mean(List<Integer> list) {
        // 'average' is undefined if there are no elements in the list.
        if (list == null || list.isEmpty())
            return 0.0;
        // Calculate the summation of the elements in the list
        long sum = 0;
        int n = list.size();
        // Iterating manually is faster than using an enhanced for loop.
        for (int i = 0; i < n; i++)
            sum += list.get(i);
        // We don't want to perform an integer division, so the cast is mandatory.
        return ((double) sum) / n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RSSData)) return false;

        RSSData rssData = (RSSData) o;

        return values.equals(rssData.values);

    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return "RSSData{" +
                "values=" + Arrays.toString(values.toArray()) +
                ", mean=" + mean +
                ", variances=" + Arrays.toString(variances.toArray()) +
                '}';
    }
}
