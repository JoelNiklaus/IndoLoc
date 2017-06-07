package ch.joelniklaus.indoloc.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Data object containing the ratings of all the tested classifiers.
 * The list is sorted by accuracy.
 * <p>
 * Created by joelniklaus on 26.11.16.
 */

public class AccuracyStatistics {

    private ArrayList<AccuracyRating> ratings = new ArrayList<>();

    public AccuracyStatistics() {

    }

    public AccuracyStatistics(ArrayList<AccuracyRating> ratings) {
        this.ratings = ratings;
    }

    /**
     * Prints the statistics including the confusion matrices.
     */
    public void printStatistics() {
        sortByAccuracy();

        for (AccuracyRating accuracyRating : ratings) {
            System.out.println("Classifier: " + accuracyRating.getName() + ", Accuracy: " + accuracyRating.getAccuracy());
            try {
                System.out.println(accuracyRating.getEvaluation().toMatrixString(accuracyRating.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n\n");
    }

    public void add(AccuracyRating accuracyRating) {
        ratings.add(accuracyRating);
    }

    public void reverse() {
        Collections.reverse(ratings);
    }

    /**
     * Sorts the statistics by accuracy.
     */
    public void sortByAccuracy() {
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(AccuracyRating::getAccuracy));
        Collections.sort(ratings, new Comparator<AccuracyRating>() {
            public int compare(AccuracyRating o1, AccuracyRating o2) {
                if (o1.getAccuracy() == o2.getAccuracy())
                    return 0;
                return o1.getAccuracy() > o2.getAccuracy() ? -1 : 1;
            }
        });
    }


}
