package ch.joelniklaus.indoloc;

import android.support.annotation.NonNull;

import org.junit.Before;

import java.util.ArrayList;
import java.util.Random;

import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.exceptions.DifferentHeaderException;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.Timer;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.statistics.AccuracyRating;
import ch.joelniklaus.indoloc.statistics.AccuracyStatistics;
import ch.joelniklaus.indoloc.statistics.PerformanceRating;
import ch.joelniklaus.indoloc.statistics.PerformanceStatistics;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.EnsembleSelection;
import weka.classifiers.meta.Grading;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.RandomSubSpace;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

/**
 * (Provides infrastructure which can be used by most of the other tests.)
 *
 * @author joelniklaus
 */
public abstract class AbstractTest {


    protected final Timer timer = new Timer();

    protected final FileHelper fileHelper = new FileHelper();

    protected final ArrayList<Classifier> classifiers = new ArrayList<>();

    protected Instances train;
    protected Instances test;

    public static final String ASSETS_PATH = "/Users/joelniklaus/Google Drive/Studium/Bachelor/Informatik/Bachelorarbeit/Code/IndoLoc/app/src/main/assets/";
    public static final String ENDING = ".arff";

    @Before
    public void setUp() throws Exception, CouldNotLoadArffException {
        fetchData();

        addClassifiers();

        // conduct one first experiment without any changes as a starting point for comparison
        System.out.println("===== Starting Experiment =====");
        conductPerformanceExperiment(train, test);
    }

    /**
     * Can be used in the subclasses to load the datasets
     *
     * @throws Exception
     * @throws CouldNotLoadArffException
     */
    protected abstract void fetchData() throws Exception, CouldNotLoadArffException;

    /**
     * Loads the train and test sets
     *
     * @param trainPath
     * @param testPath
     * @throws Exception
     * @throws CouldNotLoadArffException
     */
    protected void loadFiles(String trainPath, String testPath) throws Exception, CouldNotLoadArffException {
        train = loadFile(trainPath);
        test = loadFile(testPath);
    }

    /**
     * Loads a dataset and prepares it
     *
     * @param fileName
     * @return
     * @throws Exception
     * @throws CouldNotLoadArffException
     */
    protected Instances loadFile(String fileName) throws Exception, CouldNotLoadArffException {
        Instances data = fileHelper.loadArff(getFilePath(fileName));
        //data = WekaHelper.removeDuplicates(data);
        // prepare data
        int seed = 0;       // the seed for randomizing the data
        // randomize data
        data = randomizeData(data, seed);

        return data;
    }

    /**
     * Gets the filepath of the dataset
     *
     * @param fileName
     * @return
     */
    public String getFilePath(String fileName) {
        return ASSETS_PATH + fileName + ENDING;
    }

    /**
     * Here the classifiers to be tested can be specified. The parametrization of the classifiers also happens here.
     *
     * @throws Exception
     */
    private void addClassifiers() throws Exception {
        /* ==============================
        Functions
        ============================== */

        // Logistic Regression
        Logistic logistic = new Logistic();
        //classifiers.add(logistic);

        // Support Vector Machine
        Classifier libSVM = new LibSVM();
        //classifiers.add(libSVM);

        // Sequential Minimal Optimization
        Classifier smo = new SMO();
        classifiers.add(smo);

        /* ==============================
        Lazy
        ============================== */

        // K nearest neighbour (Auto Weka Suggestion 30 min)
        String[] iBkOptions = {"-K", "4", "-I"};
        IBk iBk = new IBk();
        iBk.setOptions(iBkOptions);
        //classifiers.add(iBk);

        // KStar (Auto Weka Suggestion 5 min) -> very slow in testing
        String[] kStarOptions = {"-B", "59", "-M", "m"};
        KStar kStar = new KStar();
        kStar.setOptions(kStarOptions);
        //classifiers.add(kStar);

        // LWL (Auto Weka Suggestion 30 min)
        String[] lwlOptions = {"-K", "30", "-A", "weka.core.neighboursearch.LinearNNSearch", "-W", "weka.classifiers.bayes.NaiveBayes", "--"};
        LWL lwl = new LWL();
        //lwl.setOptions(lwlOptions);

        /* ==============================
        Bayes
        ============================== */

        // Naive Bayes
        NaiveBayes naiveBayes = new NaiveBayes();
        classifiers.add(naiveBayes);

        // Bayes Net (descretizing data)
        BayesNet bayesNet = new BayesNet();
        //classifiers.add(bayesNet);


        /* ==============================
        Trees
        ============================== */

        // J48 Tree
        J48 j48 = new J48();
        //classifiers.add(j48);

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
        //classifiers.add(logitBoost);

        // Adaptive Boosting
        AdaBoostM1 adaBoostM1 = new AdaBoostM1();
        //classifiers.add(adaBoostM1);

        // Bagging
        Bagging bagging = new Bagging();
        //classifiers.add(bagging);

        // Voting -> very bad
        Vote vote = new Vote();
        //classifiers.add(vote);

        // Stacking -> very bad
        Stacking stacking = new Stacking();
        //classifiers.add(stacking);

        // Decorate -> very slow in training
        Decorate decorate = new Decorate();
        //classifiers.add(decorate);

        // Dagging
        Dagging dagging = new Dagging();
        classifiers.add(dagging);

        // Grading -> very bad
        Grading grading = new Grading();
        //classifiers.add(grading);

        // Ensemble Selection -> some warnings
        EnsembleSelection ensembleSelection = new EnsembleSelection();
        //classifiers.add(ensembleSelection);

        // Random Sub Space (Auto Weka Suggestion 10 min)
        RandomSubSpace randomSubSpace = new RandomSubSpace();
        String[] options = {"-I", "14", "-P", "0.620718940248979", "-S", "1", "-W", "weka.classifiers.trees.RandomForest", "--", "-I", "2", "-K", "11", "-depth", "0"};
        randomSubSpace.setOptions(options);
        //classifiers.add(randomSubSpace);

        /* ==============================
        Neural Network
        ============================== */

        // Multilayer Perceptron -> relatively slow in training
        MultilayerPerceptron mlp = new MultilayerPerceptron();
        //Setting Parameters
        mlp.setLearningRate(0.1);
        mlp.setMomentum(0.2);
        mlp.setTrainingTime(50);
        mlp.setHiddenLayers("3");
        //classifiers.add(mlp);
    }

    /**
     * Conducts an experiment focusing on accuracy. High execution speed, contains confusion matrices.
     *
     * @param train
     * @param test
     * @param crossValidation determines if the experiment should be conducted with crossvalidation (true) or with train/test set (false)
     * @return
     * @throws Exception
     */
    protected AccuracyStatistics conductAccuracyExperiment(Instances train, Instances test, boolean crossValidation) throws Exception {
        Instances data = null;
        if (crossValidation) {
            try {
                data = WekaHelper.mergeInstances(train, test);
            } catch (DifferentHeaderException e) {
                e.printStackTrace();
            }
        }

        AccuracyStatistics accuracyStatistics = new AccuracyStatistics();

        for (Classifier classifier : classifiers) {
            System.out.println("Evaluating " + classifier.getClass().getSimpleName());
            Evaluation evaluation = null;
            if (crossValidation)
                evaluation = crossValidateClassifier(classifier, data);
            else
                evaluation = trainTestClassifier(classifier, train, test);

            accuracyStatistics.add(new AccuracyRating(classifier.getClass().getSimpleName(), evaluation));
        }

        System.out.println(train.numInstances() + " training instances, " + test.numInstances() + " testing instances");
        accuracyStatistics.printStatistics();

        return accuracyStatistics;
    }

    /**
     * Evaluates a classifier with a train and test set.
     *
     * @param classifier
     * @param train
     * @param test
     * @return
     * @throws Exception
     */
    protected Evaluation trainTestClassifier(Classifier classifier, Instances train, Instances test) throws Exception {
        classifier.buildClassifier(train);
        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(classifier, test);

        return evaluation;
    }

    /**
     * Evaluates a classifier with crossvalidation
     *
     * @param classifier
     * @param data
     * @return
     * @throws Exception
     */
    protected Evaluation crossValidateClassifier(Classifier classifier, Instances data) throws Exception {
        int folds = 10;     // the number of folds to generate, >=2

        if (data.classAttribute().isNominal())
            data.stratify(folds);

        // perform cross-validation
        Evaluation evaluation = new Evaluation(data);
        for (int n = 0; n < folds; n++) {
            Instances train = data.trainCV(folds, n);
            Instances test = data.testCV(folds, n);

            // build and evaluate classifier
            classifier.buildClassifier(train);
            evaluation.evaluateModel(classifier, test);
        }

        /*
        // output evaluation
        System.out.println();
        System.out.println("=== Setup ===");
        System.out.println();
        System.out.println(evaluation.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
        */

        return evaluation;
    }

    /**
     * Randomizes a dataset
     *
     * @param data
     * @param seed
     * @return
     */
    @NonNull
    protected Instances randomizeData(Instances data, int seed) {
        Random rand = new Random(seed);
        Instances randData = new Instances(data);
        randData.randomize(rand);

        return randData;
    }

    /**
     * Conducts an experiment focusing on the performance.
     * Low execution speed, tabular overview format, can be sorted by accuracy (default), train time or test time.
     *
     * @param train
     * @param test
     * @return
     * @throws Exception
     */
    protected PerformanceStatistics conductPerformanceExperiment(Instances train, Instances test) throws Exception {
        PerformanceStatistics statistics = new PerformanceStatistics();
        for (Classifier classifier : classifiers) {
            PerformanceRating performanceRating = testClassifierPerformance(classifier, train, test);
            statistics.add(performanceRating);
        }

        System.out.println(train.numInstances() + " training instances, " + test.numInstances() + " testing instances");
        statistics.sortByAccuracy();
        statistics.printStatistics();

        return statistics;
    }

    /**
     * Tests the performance of a classifier.
     *
     * @param classifier
     * @param train
     * @param test
     * @return
     * @throws Exception
     */
    @NonNull
    protected PerformanceRating testClassifierPerformance(Classifier classifier, Instances train, Instances test) throws Exception {
        final int NUMBER_OF_TEST_ROUNDS = 5;

        long trainTimeSum = 0;
        long testTimeSum = 0;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
            // Training
            timer.reset();
            classifier = WekaHelper.train(train, classifier);
            // mean training time per instance
            trainTimeSum += timer.timeElapsedMicroS() / train.numInstances();

            // Testing
            timer.reset();
            WekaHelper.test(test, classifier);
            // mean testing time per instance
            testTimeSum += timer.timeElapsedMicroS() / test.numInstances();
        }
        // Evaluation
        Evaluation evaluation = WekaHelper.evaluate(train, test, classifier);

        double meanTrainTime = trainTimeSum / NUMBER_OF_TEST_ROUNDS;
        double meanTestTime = testTimeSum / NUMBER_OF_TEST_ROUNDS;

        return new PerformanceRating(classifier.getClass().getSimpleName(), evaluation.pctCorrect(), meanTestTime, meanTrainTime);
    }

}