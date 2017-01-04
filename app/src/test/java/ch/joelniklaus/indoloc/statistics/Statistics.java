package ch.joelniklaus.indoloc.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by joelniklaus on 26.11.16.
 */

public class Statistics {
    private ArrayList<ClassifierRating> classifierRatingList = new ArrayList<>();

    public void print() {
        printStatistics();
        printConfusionMatrices();
    }

    public void printStatistics() {
        String format = "| %-15s |  %2.2f %% |  %5.0f µs |   %5.0f µs |";

        System.out.format("+=================+==========+===========+============+%n");
        System.out.format("| Classifier Name | Accuracy | Test Time | Train Time |%n");
        System.out.format("+=================+==========+===========+============+%n");

        for (ClassifierRating classifierRating : classifierRatingList) {
            System.out.format(format, classifierRating.getName(), classifierRating.getMeanAccuracy(), classifierRating.getMeanTestTime(), classifierRating.getMeanTrainTime(), classifierRating.getEvaluation().confusionMatrix());
            System.out.format("%n+-----------------+----------+-----------+------------+%n");
        }
    }

    public void printConfusionMatrices() {
        for (ClassifierRating classifierRating : classifierRatingList) {
            try {
                System.out.println(classifierRating.getEvaluation().toMatrixString(classifierRating.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            double[][] matrix = classifierRating.getEvaluation().confusionMatrix();

            System.out.println("\n\n" + classifierRating.getName() + ": ");
            System.out.println("+======================================+");
            System.out.println("| Confusion Matrix | Predicted Classes |");
            System.out.println("+======================================+");
            for (int row = 0; row < matrix.length; row++) {
                System.out.print("| Actual Class " + (row + 1) + "   |");
                for (int col = 0; col < matrix[row].length; col++) {
                    System.out.printf(" %4.0f |", matrix[row][col]);
                }
                System.out.println();
            }
            System.out.println("+==========================================+\n");
            */
        }
    }

    public void printMatrix(int[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.printf("%4d", matrix[row][col]);
            }
            System.out.println();
        }
    }

    public ClassifierRating get(int index) {
        return classifierRatingList.get(index);
    }

    public ArrayList<ClassifierRating> getList() {
        return classifierRatingList;
    }

    public void add(ClassifierRating classifierRating) {
        classifierRatingList.add(classifierRating);
    }

    public void reverse() {
        Collections.reverse(classifierRatingList);
    }

    public void sortByAccuracy() {
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(ClassifierRating::getMeanAccuracy));
        Collections.sort(classifierRatingList, new Comparator<ClassifierRating>() {
            public int compare(ClassifierRating o1, ClassifierRating o2) {
                if (o1.getMeanAccuracy() == o2.getMeanAccuracy())
                    return 0;
                return o1.getMeanAccuracy() > o2.getMeanAccuracy() ? -1 : 1;
            }
        });
    }

    public void sortByMeanTestTime() {
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(ClassifierRating::getMeanTestTime));
        Collections.sort(classifierRatingList, new Comparator<ClassifierRating>() {
            public int compare(ClassifierRating o1, ClassifierRating o2) {
                if (o1.getMeanTestTime() == o2.getMeanTestTime())
                    return 0;
                return o1.getMeanTestTime() > o2.getMeanTestTime() ? -1 : 1;
            }
        });
    }

    public void sortByMeanTrainTime() {
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(ClassifierRating::getMeanTrainTime));
        Collections.sort(classifierRatingList, new Comparator<ClassifierRating>() {
            public int compare(ClassifierRating o1, ClassifierRating o2) {
                if (o1.getMeanTrainTime() == o2.getMeanTrainTime())
                    return 0;
                return o1.getMeanTrainTime() > o2.getMeanTrainTime() ? -1 : 1;
            }
        });
    }
}
