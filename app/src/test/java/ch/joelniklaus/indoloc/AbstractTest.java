package ch.joelniklaus.indoloc;

import android.support.annotation.NonNull;

import org.junit.Before;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.Timer;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.statistics.ClassifierRating;
import ch.joelniklaus.indoloc.statistics.Statistics;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.EnsembleSelection;
import weka.classifiers.meta.Grading;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;

import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AbstractTest {

    protected final int NUMBER_OF_TEST_ROUNDS = 5;

    protected Timer timer = new Timer();

    protected WekaHelper wekaHelper = new WekaHelper();
    protected FileHelper fileHelper = new FileHelper();

    protected ArrayList<Classifier> classifiers = new ArrayList<Classifier>();

    public static final String ASSETS_PATH = "/Users/joelniklaus/Google Drive/Studium/Bachelor/Informatik/Bachelorarbeit/Code/IndoLoc/app/src/main/assets/";
    public static final String ENDING = ".arff";

    @Before
    public void setUp() throws Exception {
        addClassifiers();

        /*
        data = fileHelper.loadArff(filePath);

        int numInstances = data.numInstances();
        RemovePercentage remove = wekaHelper.randomizeAndGetRemovePercentage(data);
        train = wekaHelper.getTrainingSet(data, remove);
        test = wekaHelper.getTestingSet(data, remove);

        assertEquals(data.numInstances(), numInstances);

        // Train Classifiers
        for (int i = 0; i < classifiers.size(); i++)
            trainedClassifiers.add(i, wekaHelper.train(train, classifiers.get(i)));
            */
    }

    protected Instances loadFile(String fileName) throws Exception {
        return fileHelper.loadArff(getFilePath(fileName));
    }

    public String getFilePath(String fileName) {
        return ASSETS_PATH + fileName + ENDING;
    }

    private void addClassifiers() throws Exception {
        /* ==============================
        Functions
        ============================== */

        // Logistic Regression
        Logistic logistic = new Logistic();
        classifiers.add(logistic);

        // Support Vector Machine
        Classifier libSVM = new LibSVM();
        classifiers.add(libSVM);

        /* ==============================
        Lazy
        ============================== */

        // K nearest neighbour (Auto Weka Suggestion 30 min)
        String[] iBkOptions = {"-K", "4", "-I"};
        IBk iBk = new IBk();
        iBk.setOptions(iBkOptions);
        classifiers.add(iBk);

        // KStar (Auto Weka Suggestion 5 min)
        String[] kStarOptions = {"-B", "59", "-M", "m"};
        KStar kStar = new KStar();
        kStar.setOptions(kStarOptions);
        //classifiers.add(kStar);

        /* ==============================
        Bayes
        ============================== */

        // Naive Bayes
        NaiveBayes naiveBayes = new NaiveBayes();
        //classifiers.add(naiveBayes);

        // Bayes Net
        BayesNet bayesNet = new BayesNet();
        //classifiers.add(bayesNet);


        /* ==============================
        Trees
        ============================== */

        // J48 Tree
        J48 j48 = new J48();
        classifiers.add(j48);

        // Random Forest (Auto Weka Suggestion 10 min)
        String[] randomForestOptions = {"-I", "10", "-K", "0", "-depth", "0"};
        RandomForest randomForest = new RandomForest();
        randomForest.setOptions(randomForestOptions);
        classifiers.add(randomForest);

        /* ==============================
        Meta
        ============================== */

        // Logistic Boosting
        LogitBoost logitBoost = new LogitBoost();
        classifiers.add(logitBoost);

        // Adaptive Boosting
        AdaBoostM1 adaBoostM1 = new AdaBoostM1();
        classifiers.add(adaBoostM1);

        // Bagging
        Bagging bagging = new Bagging();
        classifiers.add(bagging);

        // Voting
        Vote vote = new Vote();
        classifiers.add(vote);

        // Stacking
        Stacking stacking = new Stacking();
        classifiers.add(stacking);

        // Decorate
        Decorate decorate = new Decorate();
        //classifiers.add(decorate);

        // Dagging
        Dagging dagging = new Dagging();
        //classifiers.add(dagging);

        // Grading
        Grading grading = new Grading();
        classifiers.add(grading);

        // Ensemble Selection
        EnsembleSelection ensembleSelection = new EnsembleSelection();
        //classifiers.add(ensembleSelection);
    }



    protected Statistics sortAndPrintStatistics(Statistics statistics) throws Exception {
        statistics.sortByAccuracy();

        statistics.print();

        return statistics;
    }

    protected Statistics getClassifierRatings(Instances data) throws Exception {
        Statistics statistics = new Statistics();
        for (Classifier classifier : classifiers) {
            ClassifierRating classifierRating = testClassifier(classifier, data);
            statistics.add(classifierRating);
        }
        return statistics;
    }

    protected Statistics getClassifierRatings(Instances train, Instances test) throws Exception {
        Statistics statistics = new Statistics();
        for (Classifier classifier : classifiers) {
            ClassifierRating classifierRating = testClassifier(classifier, train, test);
            statistics.add(classifierRating);
        }
        return statistics;
    }

    @NonNull
    protected ClassifierRating testClassifier(Classifier classifier, Instances train, Instances test) throws Exception {
        double correctPctSum = 0;
        long trainTimeSum = 0;
        long testTimeSum = 0;
        Evaluation lastEvaluation = null;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
            // Training
            timer.reset();
            classifier = wekaHelper.train(train, classifier);
            // mean training time per instance
            trainTimeSum += timer.timeElapsedMicroS() / train.numInstances();

            // Testing
            timer.reset();
            wekaHelper.test(test, classifier);
            // mean testing time per instance
            testTimeSum += timer.timeElapsedMicroS() / test.numInstances();

            // Evaluation
            lastEvaluation = wekaHelper.evaluate(train, test, classifier);
            correctPctSum += lastEvaluation.pctCorrect();
        }
        double meanTrainTime = trainTimeSum / NUMBER_OF_TEST_ROUNDS;
        double meanTestTime = testTimeSum / NUMBER_OF_TEST_ROUNDS;
        double meanAccuracy = correctPctSum / NUMBER_OF_TEST_ROUNDS;

        return new ClassifierRating(classifier.getClass().getSimpleName(), meanAccuracy, meanTestTime, meanTrainTime, lastEvaluation);
    }

    @NonNull
    protected ClassifierRating testClassifier(Classifier classifier, Instances data) throws Exception {
        double correctPctSum = 0;
        long trainTimeSum = 0;
        long testTimeSum = 0;
        Evaluation lastEvaluation = null;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
            // Generate new Training and Testing set
            RemovePercentage remove = wekaHelper.randomizeAndGetRemovePercentage(data);
            Instances train = wekaHelper.getTrainingSet(data, remove);
            Instances test = wekaHelper.getTestingSet(data, remove);

            // Training
            timer.reset();
            classifier = wekaHelper.train(train, classifier);
            // mean training time per instance
            trainTimeSum += timer.timeElapsedMicroS() / train.numInstances();

            // Testing
            timer.reset();
            wekaHelper.test(test, classifier);
            // mean testing time per instance
            testTimeSum += timer.timeElapsedMicroS() / test.numInstances();

            // Evaluation
            lastEvaluation = wekaHelper.evaluate(train, test, classifier);
            correctPctSum += lastEvaluation.pctCorrect();
        }
        double meanTrainTime = trainTimeSum / NUMBER_OF_TEST_ROUNDS;
        double meanTestTime = testTimeSum / NUMBER_OF_TEST_ROUNDS;
        double meanAccuracy = correctPctSum / NUMBER_OF_TEST_ROUNDS;

        return new ClassifierRating(classifier.getClass().getSimpleName(), meanAccuracy, meanTestTime, meanTrainTime, lastEvaluation);
    }

    protected void testWithAndWithout(Instances withTrain, Instances withTest, Instances withoutTrain, Instances withoutTest) throws Exception {
        // With
        Statistics ratingsWith = getClassifierRatings(withTrain, withTest);
        // Without
        Statistics ratingsWithout = getClassifierRatings(withoutTrain, withoutTest);

        withAndWithout(ratingsWith, ratingsWithout);
    }

    protected void testWithAndWithout(Instances with, Instances without) throws Exception {
        // With
        Statistics ratingsWith = getClassifierRatings(with);
        // Without
        Statistics ratingsWithout = getClassifierRatings(without);

        withAndWithout(ratingsWith, ratingsWithout);
    }

    private void withAndWithout(Statistics ratingsWith, Statistics ratingsWithout) throws Exception {
        // Test each Classifier
        for (int i = 0; i < ratingsWith.getList().size(); i++) {
            ClassifierRating ratingWith = ratingsWith.get(i);
            ClassifierRating ratingWithout = ratingsWithout.get(i);
            assertTrue(ratingWith.getName().equals(ratingWithout.getName()));
            //assertTrue(ratingWith.getMeanAccuracy() > ratingWithout.getMeanAccuracy());
        }

        System.out.println("\n\n==========\nWith:\n==========");
        ratingsWith = sortAndPrintStatistics(ratingsWith);

        System.out.println("\n\n==========\nWithout:\n==========");
        ratingsWithout = sortAndPrintStatistics(ratingsWithout);

        // Test sorted ClassifierRatings
        ClassifierRating bestWith = ratingsWith.get(0);
        ClassifierRating bestWithout = ratingsWithout.get(0);
        assertTrue(bestWith.getMeanAccuracy() > bestWithout.getMeanAccuracy());
        //assertTrue(bestWith.getName().equals(bestWithout.getName()));
    }


}