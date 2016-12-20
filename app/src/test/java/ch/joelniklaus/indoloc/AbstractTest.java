package ch.joelniklaus.indoloc;

import org.junit.Before;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.LibSVM;
import ch.joelniklaus.indoloc.helpers.ClassifierRating;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.Timer;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.Grading;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AbstractTest {

    protected final int NUMBER_OF_TEST_ROUNDS = 10;

    protected Timer timer = new Timer();

    protected WekaHelper wekaHelper = new WekaHelper();
    protected FileHelper fileHelper = new FileHelper();

    protected ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
    protected ArrayList<Classifier> trainedClassifiers = new ArrayList<Classifier>();

    protected ArrayList<ClassifierRating> classifierRatings = new ArrayList<ClassifierRating>();

    protected Instances data, train, test;

    protected String filePath = "/Users/joelniklaus/Google Drive/Studium/Bachelor/Informatik/Bachelorarbeit/Code/IndoLoc/app/src/main/assets/data.arff";


    @Before
    public void setUp() throws Exception {
        addClassifiers();

        data = fileHelper.loadArff(filePath);
        RemovePercentage remove = wekaHelper.getRemovePercentage(data);

        train = wekaHelper.getTrainingSet(data, remove);

        test = wekaHelper.getTestingSet(data, remove);

        // Train Classifiers
        for (int i = 0; i < classifiers.size(); i++)
            trainedClassifiers.add(i, wekaHelper.train(train, classifiers.get(i)));
    }

    private void addClassifiers() throws Exception {
        /* ==============================
        Functions
        ============================== */

        // Logistic Regression
        classifiers.add(new Logistic());

        // Support Vector Machine
        Classifier libSVM = new LibSVM();
        //classifiers.add(libSVM);

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
        classifiers.add(kStar);

        /* ==============================
        Bayes
        ============================== */

        // Naive Bayes
        classifiers.add(new NaiveBayes());

        // Bayes Net
        classifiers.add(new BayesNet());


        /* ==============================
        Trees
        ============================== */

        // Ensemble methods

        // J48 Tree
        classifiers.add(new J48());

        // Random Forest (Auto Weka Suggestion 10 min)
        String[] randomForestOptions = {"-I", "10", "-K", "0", "-depth", "0"};
        RandomForest randomForest = new RandomForest();
        randomForest.setOptions(randomForestOptions);
        classifiers.add(randomForest);

        /* ==============================
        Meta
        ============================== */

        // Logistic Boosting
        classifiers.add(new LogitBoost());

        // Adaptive Boosting
        classifiers.add(new AdaBoostM1());

        // Bagging
        classifiers.add(new Bagging());

        // Voting
        classifiers.add(new Vote());

        // Stacking
        classifiers.add(new Stacking());

        // Decorate
        classifiers.add(new Decorate());

        // Dagging
        classifiers.add(new Dagging());

        // Grading
        classifiers.add(new Grading());

        // Ensemble Selection
    }
}