package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.statistics.ClassifierRating;
import ch.joelniklaus.indoloc.statistics.Statistics;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.Vote;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClassifierTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("exeter/train_landmarks", "exeter/test_landmarks");
    }

    @Test
    public void testVoting() throws Exception {
        Vote vote = new Vote();
        String[] options = {"-R", "MAJ", // Majority Vote
                //"-B", "weka.classifiers.bayes.NaiveBayes", // Classifiers
                //"-B", "weka.classifiers.trees.RandomForest",
                //   "B", "weka.classifiers.functions.LibSVM",
                //   "B", "weka.classifiers.lazy.IBk",
                //  "B", "weka.classifiers.functions.Logistic",
                // "B", "weka.classifiers.functions.SMO",
                // "B", "weka.classifiers.meta.Dagging",
                //  "B", "weka.classifiers.meta.LogitBoost",
                //   "-B", "weka.classifiers.functions.MultilayerPerceptron", "--", "-L", "0.1", "-M", "0.2", "-N", "50", "-V", "0", "-S", "0", "-E", "20", "-H", "3",
                //  "-B", "weka.classifiers.functions.MultilayerPerceptron", "--", "-L", "0.1", "-M", "0.2", "-N", "50", "-V", "0", "-S", "0", "-E", "20", "-H", "3",
                "-B", "weka.classifiers.functions.MultilayerPerceptron", "-L", "0.1", "-M", "0.2", "-N", "50", "-V", "0", "-S", "0", "-E", "20", "-H", "3"
        };
        vote.setOptions(options);

        ClassifierRating classifierRating = testClassifier(vote, train, test);
        printClassifierRating(classifierRating);
    }

    @Test
    public void testStacking() throws Exception {
        Vote vote = new Vote();
        String[] options = {"-M", "weka.classifiers.functions.Logistic", // Decision Classifier
                //"-B", "weka.classifiers.bayes.NaiveBayes", // Classifiers
                "-B", "weka.classifiers.trees.RandomForest",
                // "B", "weka.classifiers.functions.LibSVM",
                "B", "weka.classifiers.lazy.IBk",
                //   "B", "weka.classifiers.functions.Logistic",
                //  "B", "weka.classifiers.functions.SMO",
                "B", "weka.classifiers.meta.Dagging",
                //   "B", "weka.classifiers.meta.LogitBoost",
                //  "-B", "weka.classifiers.functions.MultilayerPerceptron -L 0.1 -M 0.2 -N 50 -V 0 -S 0 -E 20 -H 3",
                "-B", "weka.classifiers.functions.MultilayerPerceptron -L 0.1 -M 0.2 -N 50 -V 0 -S 0 -E 20 -H 3",
                "-B", "weka.classifiers.functions.MultilayerPerceptron -L 0.1 -M 0.2 -N 50 -V 0 -S 0 -E 20 -H 3"
        };
        vote.setOptions(options);

        ClassifierRating classifierRating = testClassifier(vote, train, test);
        printClassifierRating(classifierRating);
    }

    @Test
    public void testDagging() throws Exception {
        train = WekaHelper.removeDuplicates(train);

        MultilayerPerceptron mlp = new MultilayerPerceptron();
        //Setting Parameters
        mlp.setLearningRate(0.1);
        mlp.setMomentum(0.2);
        mlp.setTrainingTime(50);
        //mlp.setValidationSetSize(20);
        mlp.setHiddenLayers("3");

        Dagging dagging = new Dagging();
        String[] options = {
                "-W", "weka.classifiers.functions.MultilayerPerceptron", "--", "-L", "0.1", "-M", "0.2", "-N", "50", "-V", "0", "-S", "0", "-E", "20", "-H", "3"
        };
        dagging.setOptions(options);

        ClassifierRating classifierRating = testClassifier(mlp, train, test);
        printClassifierRating(classifierRating);
    }

    @Test
    public void testDifferentlyCollectedTrainAndTestSetAmountOfModelData() throws Exception {
        RemovePercentage removePercentage = WekaHelper.randomizeAndGetRemovePercentage(train);
        Instances train30Reduced = WekaHelper.getTrainingSet(train, removePercentage);
        Instances train70Reduced = WekaHelper.getTestingSet(train, removePercentage);

        System.out.println("===== Full Dataset =====");
        sortAndPrintStatistics(getClassifierRatings(train, test));

        System.out.println("===== 30% Reduced =====");
        sortAndPrintStatistics(getClassifierRatings(train30Reduced, test));

        System.out.println("===== 70% Reduced =====");
        sortAndPrintStatistics(getClassifierRatings(train70Reduced, test));

    }

    @Test
    public void testTrainAndTestSetOfSameCollectionDifferentDivision() throws Exception {
        Instances data = train;
        data.randomize(new java.util.Random());
        Instances train = data.trainCV(2, 0);
        Instances test = data.testCV(2, 0);

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testTrainAndTestSetOfSameCollection() throws Exception {
        Statistics statistics = getClassifierRatings(train);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testDifferentlyCollectedTrainAndTestSet() throws Exception {
        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testDifferentlyCollectedTrainAndTestSetMerged() throws Exception {
        Instances data = merge(train, test);

        Statistics statistics = getClassifierRatings(data);
        sortAndPrintStatistics(statistics);
    }

    private Instances merge(Instances first, Instances second) throws Exception {
        if (!first.equalHeaders(second))
            throw new Exception("The two instances have different headers");

        for (int i = 0; i < second.numInstances(); i++)
            first.add(second.instance(i));
        return first;
    }
}