package ch.joelniklaus.indoloc.experiments;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import ch.joelniklaus.indoloc.AbstractTest;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CrossValidationTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Performs a single run of cross-validation.
     *
     * @throws Exception
     */
    @Test
    public void testCrossValidationSingleRun() throws Exception {
        RandomForest cls = new RandomForest();
        Instances data = loadFile("experiments/experiment_new");
        int folds = 10;     // the number of folds to generate, >=2
        int seed = 0;       // the seed for randomizing the data

        // randomize data
        Instances randData = randomizeData(data, folds, seed);

        // perform cross-validation
        Evaluation eval = new Evaluation(randData);
        for (int n = 0; n < folds; n++) {
            Instances train = randData.trainCV(folds, n);
            Instances test = randData.testCV(folds, n);
            // the above code is used by the StratifiedRemoveFolds filter, the
            // code below by the Explorer/Experimenter:
            // Instances train = randData.trainCV(folds, n, rand);

            // build and evaluate classifier
            // Classifier clsCopy = Classifier.makeCopy(cls);
            Classifier clsCopy = SerializationUtils.clone(cls);
            clsCopy.buildClassifier(train);
            eval.evaluateModel(clsCopy, test);
        }

        // output evaluation
        System.out.println();
        System.out.println("=== Setup ===");
        outputSetup(cls, data, folds, seed);
        System.out.println();
        System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
    }



    /**
     * Performs a single run of cross-validation. Outputs the Confusion matrices
     * for each single fold.
     *
     * @throws Exception
     */
    @Test
    public void testCrossValidationSingleRunVariant() throws Exception {
        RandomForest cls = new RandomForest();
        Instances data = loadFile("experiments/experiment_new");
        int folds = 10;     // the number of folds to generate, >=2
        int seed = 0;       // the seed for randomizing the data

        // randomize data
        Instances randData = randomizeData(data, folds, seed);

        // perform cross-validation
        System.out.println();
        System.out.println("=== Setup ===");
        outputSetup(cls, data, folds, seed);
        System.out.println();
        Evaluation evalAll = new Evaluation(randData);
        for (int n = 0; n < folds; n++) {
            Evaluation eval = new Evaluation(randData);
            Instances train = randData.trainCV(folds, n);
            Instances test = randData.testCV(folds, n);
            // the above code is used by the StratifiedRemoveFolds filter, the
            // code below by the Explorer/Experimenter:
            // Instances train = randData.trainCV(folds, n, rand);

            // build and evaluate classifier
            // Classifier clsCopy = Classifier.makeCopy(cls);
            Classifier clsCopy = SerializationUtils.clone(cls);
            clsCopy.buildClassifier(train);
            eval.evaluateModel(clsCopy, test);
            evalAll.evaluateModel(clsCopy, test);

            // output evaluation
            System.out.println();
            System.out.println(eval.toMatrixString("=== Confusion matrix for fold " + (n + 1) + "/" + folds + " ===\n"));
        }

        // output evaluation
        System.out.println();
        System.out.println(evalAll.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
    }

    /**
     * Performs a single run of cross-validation and adds the prediction on the
     * test set to the dataset.
     *
     * @throws Exception
     */
    @Test
    public void testCrossValidationSingleRunPrediction() throws Exception {
        RandomForest cls = new RandomForest();
        Instances data = loadFile("experiments/experiment_new");
        int folds = 10;     // the number of folds to generate, >=2
        int seed = 0;       // the seed for randomizing the data

        // randomize data
        Instances randData = randomizeData(data, folds, seed);

        // perform cross-validation and add predictions
        Instances predictedData = null;
        Evaluation eval = new Evaluation(randData);
        for (int n = 0; n < folds; n++) {
            Instances train = randData.trainCV(folds, n);
            Instances test = randData.testCV(folds, n);
            // the above code is used by the StratifiedRemoveFolds filter, the
            // code below by the Explorer/Experimenter:
            // Instances train = randData.trainCV(folds, n, rand);

            // build and evaluate classifier
            // Classifier clsCopy = Classifier.makeCopy(cls);
            Classifier clsCopy = SerializationUtils.clone(cls);
            clsCopy.buildClassifier(train);
            eval.evaluateModel(clsCopy, test);

            // add predictions
            AddClassification filter = new AddClassification();
            filter.setClassifier(cls);
            filter.setOutputClassification(true);
            filter.setOutputDistribution(true);
            filter.setOutputErrorFlag(true);
            filter.setInputFormat(train);
            Filter.useFilter(train, filter);  // trains the classifier
            Instances pred = Filter.useFilter(test, filter);  // perform predictions on test set
            if (predictedData == null)
                predictedData = new Instances(pred, 0);
            for (int j = 0; j < pred.numInstances(); j++)
                predictedData.add(pred.instance(j));
        }

        // output evaluation
        System.out.println();
        System.out.println("=== Setup ===");
        outputSetup(cls, data, folds, seed);
        System.out.println();
        System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));

        // output "enriched" dataset
        System.out.println(predictedData.toSummaryString());
    }


    /**
     * Performs multiple runs of cross-validation.
     *
     * @throws Exception
     */
    @Test
    public void testCrossValidationMultipleRuns() throws Exception {
        RandomForest cls = new RandomForest();
        Instances data = loadFile("experiments/experiment_new");
        int folds = 10;     // the number of folds to generate, >=2
        int runs = 5;       // the number of runs

        // perform cross-validation
        for (int i = 0; i < runs; i++) {
            // randomize data
            int seed = i + 1;
            Instances randData = randomizeData(data, folds, seed);

            Evaluation eval = new Evaluation(randData);
            for (int n = 0; n < folds; n++) {
                Instances train = randData.trainCV(folds, n);
                Instances test = randData.testCV(folds, n);
                // the above code is used by the StratifiedRemoveFolds filter, the
                // code below by the Explorer/Experimenter:
                // Instances train = randData.trainCV(folds, n, rand);

                // build and evaluate classifier
                // Classifier clsCopy = Classifier.makeCopy(cls);
                Classifier clsCopy = SerializationUtils.clone(cls);
                clsCopy.buildClassifier(train);
                eval.evaluateModel(clsCopy, test);
            }

            // output evaluation
            System.out.println();
            System.out.println("=== Setup run " + (i + 1) + " ===");
            outputSetup(cls, data, folds, seed);
            System.out.println();
            System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation run " + (i + 1) + " ===", false));
        }
    }

    private void outputSetup(RandomForest cls, Instances data, int folds, int seed) {
        System.out.println("Classifier: " + cls.getClass().getSimpleName() + " " + Utils.joinOptions(cls.getOptions()));
        System.out.println("Dataset: " + data.relationName());
        System.out.println("Folds: " + folds);
        System.out.println("Seed: " + seed);
    }

    @NonNull
    private Instances randomizeData(Instances data, int folds, int seed) {
        Random rand = new Random(seed);
        Instances randData = new Instances(data);
        randData.randomize(rand);
        if (randData.classAttribute().isNominal())
            randData.stratify(folds);
        return randData;
    }

}