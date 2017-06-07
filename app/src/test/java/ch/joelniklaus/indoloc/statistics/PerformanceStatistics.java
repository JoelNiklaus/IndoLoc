package ch.joelniklaus.indoloc.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.joelniklaus.indoloc.helpers.AbstractHelper;

/**
 * Data object containing the ratings of all the tested classifiers.
 * The list can be sorted by accuracy (default), train time and test time.
 * <p>
 * Created by joelniklaus on 26.11.16.
 */

public class PerformanceStatistics {

    private ArrayList<PerformanceRating> ratings = new ArrayList<>();

    public PerformanceStatistics() {

    }

    public PerformanceStatistics(ArrayList<PerformanceRating> ratings) {
        this.ratings = ratings;
        sortByAccuracy();
    }

    /**
     * Prints the statistics in a tabular overview format.
     */
    public void printStatisticsTabular() {
        String format = "| %-25s |  %2.2f %% |  %5.0f µs |   %5.0f µs |";

        System.out.format("+===========================+==========+===========+============+%n");
        System.out.format("| Classifier Name           | Accuracy | Test Time | Train Time |%n");
        System.out.format("+===========================+==========+===========+============+%n");

        for (PerformanceRating performanceRating : ratings) {
            System.out.format(format, performanceRating.getName(), performanceRating.getAccuracy(), performanceRating.getMeanTestTime(), performanceRating.getMeanTrainTime());
            System.out.format("%n+---------------------------+----------+-----------+------------+%n");
        }

        System.out.println("\n\n");
    }

    /**
     * Prints the statistics in csv format.
     */
    public void printStatisticsCSV() {
        System.out.println("Classifier Name, Accuracy, Test Time, Train Time");

        for (PerformanceRating performanceRating : ratings)
            System.out.println(performanceRating.getName()
                    + ", " + AbstractHelper.round((float) performanceRating.getAccuracy(), 2)
                    + ", " + performanceRating.getMeanTestTime()
                    + ", " + performanceRating.getMeanTrainTime());

        System.out.println("\n\n");
    }


    public void add(PerformanceRating performanceRating) {
        ratings.add(performanceRating);
        sortByAccuracy();
    }

    public void reverse() {
        Collections.reverse(ratings);
    }

    /**
     * Sorts the table by accuracy.
     */
    public void sortByAccuracy() {
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(PerformanceRating::getMeanAccuracy));
        Collections.sort(ratings, new Comparator<PerformanceRating>() {
            public int compare(PerformanceRating o1, PerformanceRating o2) {
                if (o1.getAccuracy() == o2.getAccuracy())
                    return 0;
                return o1.getAccuracy() > o2.getAccuracy() ? -1 : 1;
            }
        });
    }

    /**
     * Sorts the table by test time.
     */
    public void sortByMeanTestTime() {
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(PerformanceRating::getMeanTestTime));
        Collections.sort(ratings, new Comparator<PerformanceRating>() {
            public int compare(PerformanceRating o1, PerformanceRating o2) {
                if (o1.getMeanTestTime() == o2.getMeanTestTime())
                    return 0;
                return o1.getMeanTestTime() > o2.getMeanTestTime() ? -1 : 1;
            }
        });
    }

    /**
     * Sorts the table by train time.
     */
    public void sortByMeanTrainTime() {
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(PerformanceRating::getMeanTrainTime));
        Collections.sort(ratings, new Comparator<PerformanceRating>() {
            public int compare(PerformanceRating o1, PerformanceRating o2) {
                if (o1.getMeanTrainTime() == o2.getMeanTrainTime())
                    return 0;
                return o1.getMeanTrainTime() > o2.getMeanTrainTime() ? -1 : 1;
            }
        });
    }
}
