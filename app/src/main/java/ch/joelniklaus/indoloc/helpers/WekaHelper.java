package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import ch.joelniklaus.indoloc.BuildConfig;
import ch.joelniklaus.indoloc.models.DataPoint;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.unsupervised.instance.RemoveWithValues;

// Weka version = 3.7.3
// weka_old.jar

public class WekaHelper {

    private Context context;

    private Classifier classifier;

    private final Timer timer = new Timer();


    public static final String TRAINING_SET_PERCENTAGE = "70";


    public WekaHelper() {

    }

    public WekaHelper(Context context) {
        this.context = context;
    }

    public static Evaluation evaluate(Instances data, Classifier classifier) throws Exception {
        RemovePercentage remove = randomizeAndGetRemovePercentage(data);
        return evaluate(getTrainingSet(data, remove), getTestingSet(data, remove), classifier);
    }

    public static Evaluation evaluate(Instances train, Instances test, Classifier classifier) throws Exception {
        classifier.buildClassifier(train);
        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(classifier, test);

        return evaluation;
    }


    public static void test(Instances test, Classifier classifier) throws Exception {
        for (int i = 0; i < test.numInstances(); i++)
            classifier.classifyInstance(test.instance(i));
    }

    public static Classifier train(Instances train, Classifier classifier) throws Exception {
        classifier.buildClassifier(train);
        return classifier;
    }

    public Evaluation evaluateForView(Instances data) throws Exception {
        timer.reset();

        Evaluation evaluation = evaluate(data, classifier);

        alert(evaluation.toSummaryString("Time: " + timer.timeElapsed() + "ms\n\nResults\n======\n", false));

        return evaluation;
    }

    public String testForView(Instances test) throws Exception {
        timer.reset();
        String results = "", predicted = "";
        for (int i = 0; i < test.numInstances(); i++) {
            double actualClass = test.instance(i).classValue();
            String actual = "?";
            // class is set
            if (actualClass > -1)
                actual = test.classAttribute().value((int) actualClass);

            double predictedClass = classifier.classifyInstance(test.instance(i));
            predicted = test.classAttribute().value((int) predictedClass);

            results += "Predicted: " + predicted + "-> Actual: " + actual + "\n";
        }
        //alert("Time: " + timer.timeElapsed() + "ms\n\n" + results);

        return predicted;
    }

    public static String predictInstance(Classifier classifier, Instances test) throws Exception {
        Timer timer = new Timer();

        double predictedClass = classifier.classifyInstance(test.instance(0));
        String predicted = test.classAttribute().value((int) predictedClass);

        return timer.timeElapsed() + "ms -> " + predicted;
    }

    /**
     * Randomizes the data. Divides data into training set and test set according to
     * TRAINING_SET_PERCENTAGE. Trains the classifier using the training set. Returns the test set.
     *
     * @param data
     * @return test set
     * @throws Exception
     */
    public Instances trainForView(Instances data) throws Exception {
        timer.reset();

        RemovePercentage remove = randomizeAndGetRemovePercentage(data);
        Instances train = getTrainingSet(data, remove);
        Instances test = getTestingSet(data, remove);

        buildClassifier(train);

        alert("Time: " + timer.timeElapsed() + "ms\n\nModel successfully trained: \n" + classifier.toString());
        return test;
    }

    // Change Model to be trained here!
    private void buildClassifier(Instances train) throws Exception {
        trainBagging();
        classifier.buildClassifier(train);
    }

    // K-Nearest Neighbour
    private void trainKNN() {
        classifier = new IBk();
    }

    // Support Vector Machine
    private void trainSVM() {
        classifier = new LibSVM();
    }

    // Naive Bayes
    private void trainNB() {
        classifier = new NaiveBayes();
    }

    // Logistic Regression
    private void trainLR() {
        classifier = new Logistic();
    }

    // Bagging
    private void trainBagging() {
        classifier = new Bagging();
    }

    // Boosting
    private void trainBoosting() {
        classifier = new LogitBoost();
    }

    // Random Forest
    private void trainRF() {
        classifier = new RandomForest();
    }

    @NonNull
    public static StratifiedRemoveFolds randomizeAndGetStratifiedRemoveFolds(Instances data) throws Exception {
        data.setClassIndex(0);

        // use StratifiedRemoveFolds to randomly split the data
        StratifiedRemoveFolds stratifiedRemoveFolds = new StratifiedRemoveFolds();

        // set options for creating the subset of data
        String[] options = new String[6];

        options[0] = "-N";                 // indicate we want to set the number of folds
        options[1] = Integer.toString(5);  // split the data into five random folds
        options[2] = "-F";                 // indicate we want to select a specific fold
        options[3] = Integer.toString(1);  // select the first fold
        options[4] = "-S";                 // indicate we want to set the random seed
        options[5] = Integer.toString(1);  // set the random seed to 1

        stratifiedRemoveFolds.setOptions(options);        // set the filter options
        stratifiedRemoveFolds.setInputFormat(data);       // prepare the filter for the data format
        stratifiedRemoveFolds.setInvertSelection(false);  // do not invert the selection

        return stratifiedRemoveFolds;
    }

    @NonNull
    public static Instances getTestingSet(Instances data, StratifiedRemoveFolds stratifiedRemoveFolds) throws Exception {
        Instances test = Filter.useFilter(data, stratifiedRemoveFolds);
        test.setClassIndex(0);
        return test;
    }

    @NonNull
    public static Instances getTrainingSet(Instances data, StratifiedRemoveFolds stratifiedRemoveFolds) throws Exception {
        stratifiedRemoveFolds.setInvertSelection(true);     // invert the selection to get other data
        Instances train = Filter.useFilter(data, stratifiedRemoveFolds);
        train.setClassIndex(0);
        return train;
    }


    @NonNull
    public static Instances getTestingSet(Instances data, RemovePercentage removePercentage) throws Exception {
        String[] optionsTest = {"-P", TRAINING_SET_PERCENTAGE};
        removePercentage.setOptions(optionsTest);
        Instances test = Filter.useFilter(data, removePercentage);
        test.setClassIndex(0);
        return test;
    }

    @NonNull
    public static Instances getTrainingSet(Instances data, RemovePercentage removePercentage) throws Exception {
        String[] optionsTrain = {"-P", TRAINING_SET_PERCENTAGE, "-V"};
        removePercentage.setOptions(optionsTrain);
        Instances train = Filter.useFilter(data, removePercentage);
        train.setClassIndex(0);
        return train;
    }

    /**
     * Randomizes the data and then gets a RemovePercentage used to divide the data into train and test Instances.
     *
     * @param data
     * @return
     * @throws Exception
     */
    @NonNull
    public static RemovePercentage randomizeAndGetRemovePercentage(Instances data) throws Exception {
        data.setClassIndex(0);
        // Randomizing
        data.randomize(new Random());

        // Filtering
        RemovePercentage removePercentage = new RemovePercentage();
        removePercentage.setInputFormat(data);
        return removePercentage;
    }

    /**
     * @param data
     * @param attributeIndices one Index: e.g. 1, multiple Indices: eg. 2-5
     * @return
     * @throws Exception
     */
    public static Instances removeAttributes(Instances data, String attributeIndices) throws Exception {
        String[] options = {"-R", attributeIndices};
        Remove remove = new Remove();
        remove.setInputFormat(data);
        remove.setOptions(options);
        return Filter.useFilter(data, remove);
    }

    /**
     * @param data
     * @param classValuesIndices the class values to be removed
     * @return
     * @throws Exception
     */
    public static Instances removeAllOfSpecificClassFilter(Instances data, String classValuesIndices) throws Exception {
        String[] options = {"-C", data.classIndex() + "", "-L", classValuesIndices};
        RemoveWithValues removeWithValues = new RemoveWithValues();
        removeWithValues.setInputFormat(data);
        removeWithValues.setOptions(options);
        return Filter.useFilter(data, removeWithValues);
    }

    // does not remove value in header
    public static Instances removeAllOfSpecificClass(Instances data, int classIndex) throws Exception {
        for(int i = 0; i < data.numInstances(); i++)
            if ((int) data.get(i).classValue() == classIndex) {
                data.delete(i);
                i--;
            }
        return data;
    }


    public static Instances removeDuplicates(Instances data) {
        InstanceComparator comparator = new InstanceComparator();
        for (int i = 0; i < data.numInstances() - 1; i++) {
            for (int j = i + 1; j < data.numInstances(); j++)
                if (comparator.compare(data.instance(i), data.instance(j)) == 0) {
                    data.delete(j);
                    j--;
                }
        }
        return data;
    }

    @NonNull
    public static Instances convertToSingleInstance(Instances instances, DataPoint dataPoint) {
        instances.delete();
        instances.setClassIndex(0);

        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        dataPoints.add(dataPoint);
        addInstances(dataPoints, instances);

        assertion(instances.numInstances() == 1);
        assertion(instances.classIndex() == 0);

        return instances;
    }

    @NonNull
    public static Instances buildInstances(ArrayList<DataPoint> dataPoints) {
        ArrayList<Attribute> attributes = buildAttributes(dataPoints);

        Instances data = new Instances("TestInstances", attributes, dataPoints.size());
        data.setClassIndex(0);

        addInstances(dataPoints, data);

        return data;
    }

    private static void addInstances(ArrayList<DataPoint> dataPoints, Instances data) {
        double[] instanceValues;
        for (DataPoint dataPoint : dataPoints) {
            instanceValues = new double[data.numAttributes()];
            int index = 0;

            // room
            instanceValues[index] = data.classAttribute().indexOfValue(dataPoint.getRoom());
            index++;

            // sensors
            instanceValues[index] = dataPoint.getSensorData().getMagneticY();
            index++;
            instanceValues[index] = dataPoint.getSensorData().getMagneticZ();
            index++;

            // rss values
            for (int i = 0; i < dataPoint.getRssData().getValues().size(); i++) {
                instanceValues[index] = dataPoint.getRssData().getValues().get(i);
                index++;
            }

            /*
            // rss mean
            instanceValues[index] = dataPoint.getRssData().getMean();
            index++;

            // rss variances
            for (int i = 0; i < dataPoint.getRssData().getVariances().size(); i++) {
                instanceValues[index] = dataPoint.getRssData().getVariances().get(i);
                index++;
            }
            */

            data.add(new DenseInstance(1.0, instanceValues));
        }
    }

    @NonNull
    private static ArrayList<Attribute> buildAttributes(ArrayList<DataPoint> dataPoints) {
        ArrayList<String> rooms = getRooms(dataPoints);

        // rooms + number of rss + number of sensors
        //int numberOfAttributes = 1 + CollectDataActivity.NUMBER_OF_ACCESS_POINTS + CollectDataActivity.NUMBER_OF_SENSORS;
        ArrayList<Attribute> attributes = new ArrayList<>();

        // class: room
        attributes.add(new Attribute("room", rooms));

        //assertion(dataPoints.get(0).getRssData().getValues().size() == dataPoints.get(0).getRssData().getVariances().size());

        // sensors
        attributes.add(new Attribute("magneticY", Attribute.NUMERIC));
        attributes.add(new Attribute("magneticZ", Attribute.NUMERIC));

        // rss values
        for (int i = 0; i < dataPoints.get(0).getRssData().getValues().size(); i++)
            attributes.add(new Attribute("rssValue" + i, Attribute.NUMERIC));

        /*
        // rss mean
        attributes.add(new Attribute("mean", Attribute.NUMERIC));

        // rss variances
        for (int i = 0; i < dataPoints.get(0).getRssData().getVariances().size(); i++)
            attributes.add(new Attribute("rssVariance" + i, Attribute.NUMERIC));
        */

        return attributes;
    }

    @NonNull
    private static ArrayList<String> getRooms(ArrayList<DataPoint> dataPoints) {
        ArrayList<String> rooms = new ArrayList<>();

        for (DataPoint dataPoint : dataPoints)
            if (!rooms.contains(dataPoint.getRoom()))
                rooms.add(dataPoint.getRoom());
        return rooms;
    }


    private void alert(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private static void assertion(boolean condition) {
        if (BuildConfig.DEBUG && !condition) throw new AssertionError();
    }
}
