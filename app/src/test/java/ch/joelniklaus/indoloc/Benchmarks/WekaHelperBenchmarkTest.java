package ch.joelniklaus.indoloc.Benchmarks;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.joelniklaus.indoloc.ClassifierRating;
import ch.joelniklaus.indoloc.LibSVM;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.Timer;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.AutoWEKAClassifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.unsupervised.instance.RemovePercentage;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WekaHelperBenchmarkTest {

    private Timer timer = new Timer();

    private int numberOfTestRounds = 100;

    private WekaHelper wekaHelper = new WekaHelper();
    private FileHelper fileHelper = new FileHelper();

    private ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
    private ArrayList<Classifier> trainedClassifiers = new ArrayList<Classifier>();

    private ArrayList<ClassifierRating> classifierRatings = new ArrayList<ClassifierRating>();

    private Instances data, train, test;
    String filePath = "/Users/joelniklaus/Google Drive/Studium/Bachelor/Informatik/Bachelorarbeit/Code/IndoLoc/app/src/main/assets/data.arff";


    @Before
    public void setUp() throws Exception {
        data = fileHelper.loadArff(filePath);
        RemovePercentage remove = wekaHelper.getRemovePercentage(data);

        train = wekaHelper.getTrainingSet(data, remove);

        test = wekaHelper.getTestingSet(data, remove);

        // Support Vector Machine
        classifiers.add(new LibSVM());

        // K nearest neighbour
        classifiers.add(new IBk());

        // Naive Bayes
        classifiers.add(new NaiveBayes());

        // Logistic Regression
        classifiers.add(new Logistic());

        // Random Forest
        classifiers.add(new RandomForest());

        // Ensemble methods
        classifiers.add(new LogitBoost());
        classifiers.add(new Bagging());
        classifiers.add(new AdaBoostM1());

        // Auto Weka
        //classifiers.add(new AutoWEKAClassifier());

        for (int i = 0; i < classifiers.size(); i++)
            trainedClassifiers.add(i, wekaHelper.train(train, classifiers.get(i)));
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAutoWeka() throws Exception {
        AutoWEKAClassifier autoweka = new AutoWEKAClassifier();
        autoweka.setTimeLimit(1); // in minutes
        autoweka.setMemLimit(1024); // in MB
        autoweka.setDebug(true);
        autoweka.setSeed(123);
        autoweka.buildClassifier(train);
    }

    @Test
    public void testCVParameterSelection() throws Exception {
        J48 classifier = new J48();
        System.out.println(getCorrectPctSum(classifier));
        CVParameterSelection cvParameterSelection = new CVParameterSelection();
        cvParameterSelection.setClassifier(classifier);
        cvParameterSelection.buildClassifier(train);
        cvParameterSelection.setNumFolds(5);  // using 5-fold CV
        cvParameterSelection.addCVParameter("C 0.1 0.5 5");
        String[] classifierOptions = cvParameterSelection.getBestClassifierOptions();
        classifier.setOptions(classifierOptions);
        classifier.buildClassifier(train);
        System.out.println(Utils.joinOptions(classifierOptions));
        System.out.println(getCorrectPctSum(classifier));
    }

    @Test
    public void testGridSearch() throws Exception {

    }

    @Test
    public void testMultiSearch() throws Exception {

    }

    @Test
    public void testAllClassifiersWithDefaultParameters() throws Exception {
        for (Classifier classifier : classifiers) {
            double correctPctSum = 0;
            long trainTimeSum = 0;
            long testTimeSum = 0;
            for (int round = 0; round < numberOfTestRounds; round++) {
                //Training
                timer.reset();
                classifier = wekaHelper.train(train, classifier);
                trainTimeSum += timer.timeElapsedMicroS();

                // Testing
                timer.reset();
                wekaHelper.test(test, classifier);
                testTimeSum += timer.timeElapsedMicroS();

                // Evaluation
                correctPctSum += wekaHelper.evaluate(data, classifier).pctCorrect();
            }
            double meanTrainTime = trainTimeSum / numberOfTestRounds;
            double meanTestTime = testTimeSum / numberOfTestRounds;
            double meanAccuracy = correctPctSum / numberOfTestRounds;
            classifierRatings.add(new ClassifierRating(classifier.getClass().getSimpleName(), meanAccuracy, meanTestTime, meanTrainTime));
        }
        // Sort by Accuracy
        classifierRatings.sort(Comparator.comparing(ClassifierRating::getMeanAccuracy));
        Collections.reverse(classifierRatings);
        // Display Statistics
        for (ClassifierRating classifierRating : classifierRatings)
            System.out.println(classifierRating);
    }

    @Test
    public void testAccuracy() throws Exception {
        double currentBest = 0;
        String bestClassifier = "";
        Evaluation evaluation = null;
        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            double correctPctSum = getCorrectPctSum(classifier);

            System.out.println("\n\nLast evaluation: " + classifierName + "\n\n" + wekaHelper.evaluate(data, classifier).toSummaryString());
            if (correctPctSum > currentBest) {
                currentBest = correctPctSum;
                bestClassifier = classifierName;
            }
        }

        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + "% mean correct classification\n\n");
    }

    private double getCorrectPctSum(Classifier classifier) throws Exception {
        Evaluation evaluation;
        double correctPctSum = 0;
        for (int round = 0; round < numberOfTestRounds; round++) {
            evaluation = wekaHelper.evaluate(data, classifier);
            correctPctSum += evaluation.pctCorrect();
        }
        return correctPctSum / numberOfTestRounds;
    }

    @Test
    public void testTrainPerformance() throws Exception {
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            long trainTimeSum = getMeanTrainTime(classifier);

            System.out.println("\n\nClassifier: " + classifierName + ": " + trainTimeSum + " micros mean train time\n\n");
            if (trainTimeSum < currentBest) {
                currentBest = trainTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean train time\n\n");
    }

    private long getMeanTrainTime(Classifier classifier) throws Exception {
        long trainTimeSum = 0;
        for (int round = 0; round < numberOfTestRounds; round++) {
            timer.reset();
            wekaHelper.train(train, classifier);
            trainTimeSum += timer.timeElapsedMicroS();
        }
       return trainTimeSum / numberOfTestRounds;
    }

    @Test
    public void testTestPerformance() throws Exception {
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            long testTimeSum = getMeanTestTime(classifier);

            System.out.println("\n\nClassifier: " + classifierName + ": " + testTimeSum + " micros mean test time\n\n");
            if (testTimeSum < currentBest) {
                currentBest = testTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean test time\n\n");
    }

    private long getMeanTestTime(Classifier classifier) throws Exception {
        long testTimeSum = 0;
        for (int round = 0; round < numberOfTestRounds; round++) {
            timer.reset();
            wekaHelper.test(test, classifier);
            testTimeSum += timer.timeElapsedMicroS();
        }
        return testTimeSum / numberOfTestRounds;
    }
}