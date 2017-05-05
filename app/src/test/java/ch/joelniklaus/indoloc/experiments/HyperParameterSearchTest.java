package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import java.util.Arrays;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.AutoWEKAClassifier;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.meta.GridSearch;
import weka.classifiers.meta.MultiSearch;
import weka.classifiers.meta.multisearch.DefaultEvaluationMetrics;
import weka.classifiers.trees.J48;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.ListParameter;
import weka.core.setupgenerator.MathParameter;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HyperParameterSearchTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("exeter/train_landmarks", "exeter/test_landmarks");
    }


    @Test
    public void testMultilayerPerceptron() throws Exception {
        train = WekaHelper.removeDuplicates(train);
        //train = WekaHelper.getEveryNThInstance(train, 2);

        MultilayerPerceptron mlp = new MultilayerPerceptron();
        //Setting Parameters
        mlp.setLearningRate(0.1);
        mlp.setMomentum(0.2);
        mlp.setTrainingTime(50);
        //mlp.setValidationSetSize(20);
        mlp.setHiddenLayers("3");

        /*
        PerformanceRating performanceRating = testClassifier(mlp, train, test);
        System.out.println(performanceRating.toString());
        System.out.println(performanceRating.getEvaluation().toMatrixString());
        System.out.println(Arrays.toString(mlp.getOptions()));
        */
    }

    /**
     * Explore the best value for one parameter of a provided classifier
     *
     * @throws Exception
     */
    @Test
    public void testCVParameterSelection() throws Exception {
        J48 classifier = new J48();

        CVParameterSelection cvParameterSelection = new CVParameterSelection();
        cvParameterSelection.setClassifier(classifier);
        cvParameterSelection.buildClassifier(train);
        cvParameterSelection.setNumFolds(5);  // using 5-fold CV
        cvParameterSelection.addCVParameter("C 0.1 0.5 5");
        String[] classifierOptions = cvParameterSelection.getBestClassifierOptions();
        classifier.setOptions(classifierOptions);
        classifier.buildClassifier(train);
        System.out.println(Utils.joinOptions(classifierOptions));
        System.out.println(classifier.getClass().getSimpleName() + " with Parameters: " + Arrays.toString(classifier.getOptions()));
    }

    /**
     * Explore two parameters of a provided classifier
     *
     * @throws Exception
     */
    @Test
    public void testGridSearch() throws Exception {
        MultilayerPerceptron classifier = new MultilayerPerceptron();

        GridSearch gridSearch = new GridSearch();
        gridSearch.buildClassifier(train);

        System.out.println(classifier.getClass().getSimpleName() + " with Parameters: " + Arrays.toString(classifier.getOptions()));

    }

    /**
     * Search for the best parameters for a provided classifier
     *
     * @throws Exception
     */
    @Test
    public void testMultiSearch() throws Exception {
        /*
        MultilayerPerceptron classifier = new MultilayerPerceptron();

        String[] options = {"-D", "-W", classifier.getClass().getName()};

        MultiSearch multiSearch = new MultiSearch();
        multiSearch.setOptions(options);
        multiSearch.setClassifier(classifier);

        multiSearch.buildClassifier(train);
        System.out.println(multiSearch.getBestClassifier().toString());

        System.out.println(classifier.getClass().getSimpleName() + " with Parameters: " + Arrays.toString(classifier.getOptions()));
        */


        // configure classifier we want to optimize
        MultilayerPerceptron classifier = new MultilayerPerceptron();
        classifier.setTrainingTime(2000);
        classifier.setValidationSetSize(10);

        // configure multisearch
        // Learning Rate
        MathParameter learningRate = new MathParameter();
        learningRate.setProperty("learningRate");
        learningRate.setBase(0.3);
        learningRate.setMin(0.05);
        learningRate.setMax(0.5);
        learningRate.setStep(0.05);
        learningRate.setExpression("I");

        // Momentum
        MathParameter momentum = new MathParameter();
        momentum.setProperty("momentum");
        momentum.setBase(0.2);
        momentum.setMin(0.05);
        momentum.setMax(0.5);
        momentum.setStep(0.05);
        momentum.setExpression("I");

        // Hidden Layers
        ListParameter hiddenLayers = new ListParameter();
        hiddenLayers.setProperty("hiddenLayers");
        //hiddenLayers.setCustomDelimiter(","); // Default Delimiter is probably a whitespace
        hiddenLayers.setList("a i o t 5,5,5 10,10,10,10,10");


        MultiSearch multi = new MultiSearch();
        //multi.setAlgorithm(new RandomSearch()); // set in order to search the entire search space
        multi.setClassifier(classifier);
        multi.setSearchParameters(new AbstractParameter[]{
                learningRate,
                momentum,
                hiddenLayers
        });
        SelectedTag tag = new SelectedTag(
                DefaultEvaluationMetrics.EVALUATION_AUC,
                new DefaultEvaluationMetrics().getTags());
        multi.setEvaluation(tag);

        // output configuration
        System.out.println("\nMultiSearch commandline:\n" + Utils.toCommandLine(multi));

        // optimize
        System.out.println("\nOptimizing...\n");
        multi.buildClassifier(train);
        System.out.println("Best setup:\n" + Utils.toCommandLine(multi.getBestClassifier()));
        System.out.println("Best parameter: " + multi.getGenerator().evaluate(multi.getBestValues()));
    }

    /**
     * Search for the best classifier using the best parameters for a given dataset
     *
     * @throws Exception
     */
    @Test
    public void testAutoWeka() throws Exception {
        AutoWEKAClassifier autoweka = new AutoWEKAClassifier();
        autoweka.setTimeLimit(30); // in minutes
        autoweka.setMemLimit(2048); // in MB
        autoweka.setDebug(true);
        autoweka.setSeed(123);
        autoweka.setnBestConfigs(3);
        autoweka.buildClassifier(train);
        System.out.println(autoweka.getnBestConfigs());
    }
}