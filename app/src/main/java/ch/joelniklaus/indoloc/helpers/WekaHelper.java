package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import ch.joelniklaus.indoloc.BuildConfig;
import ch.joelniklaus.indoloc.LibSVM;
import ch.joelniklaus.indoloc.models.DataPoint;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemovePercentage;

// Weka version = 3.7.3
// weka_old.jar

public class WekaHelper {

    private Context context;

    private Classifier classifier;

    private Timer timer = new Timer();


    public static final String TRAINING_SET_PERCENTAGE = "70";


    public WekaHelper() {

    }

    public WekaHelper(Context context) {
        this.context = context;
    }

    public Evaluation evaluate(Instances data, Classifier classifier) throws Exception {
        RemovePercentage remove = getRemovePercentage(data);

        Instances train = getTrainingSet(data, remove);

        Instances test = getTestingSet(data, remove);

        classifier.buildClassifier(train);

        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(classifier, test);

        return evaluation;
    }


    public void test(Instances test, Classifier classifier) throws Exception {
        for (int i = 0; i < test.numInstances(); i++)
            classifier.classifyInstance(test.instance(i));
    }

    public Classifier train(Instances train, Classifier classifier) throws Exception {
        classifier.buildClassifier(train);
        return classifier;
    }

    public Evaluation evaluateForView(Instances data) throws Exception {
        timer.reset();
        RemovePercentage remove = getRemovePercentage(data);

        Instances train = getTrainingSet(data, remove);

        Instances test = getTestingSet(data, remove);

        classifier.buildClassifier(train);

        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(classifier, test);

        alert(evaluation.toSummaryString("Time: " + timer.timeElapsed() + "ms\n\nResults\n======\n", false));

        return evaluation;
    }

    public void testForView(Instances test) throws Exception {
        timer.reset();
        String results = "";
        for (int i = 0; i < test.numInstances(); i++) {
            double actualClass = test.instance(i).classValue();
            String actual = "?";
            // class is set
            if(actualClass > -1)
                 actual = test.classAttribute().value((int) actualClass);

            double predictedClass = classifier.classifyInstance(test.instance(i));
            String predicted = test.classAttribute().value((int) predictedClass);

            results += "Predicted: " + predicted + "-> Actual: " + actual + "\n";
        }
        alert("Time: " + timer.timeElapsed() + "ms\n\n" + results);
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
        RemovePercentage remove = getRemovePercentage(data);

        Instances train = getTrainingSet(data, remove);

        Instances test = getTestingSet(data, remove);

        buildClassifier(train);

        alert("Time: " + timer.timeElapsed() + "ms\n\nModel successfully trained: \n" + classifier.toString());
        return test;
    }

    @NonNull
    public Instances getTestingSet(Instances data, RemovePercentage remove) throws Exception {
        String[] optionsTest = {"-P", TRAINING_SET_PERCENTAGE};
        remove.setOptions(optionsTest);
        Instances test = Filter.useFilter(data, remove);
        test.setClassIndex(0);
        return test;
    }

    @NonNull
    public Instances getTrainingSet(Instances data, RemovePercentage remove) throws Exception {
        String[] optionsTrain = {"-P", TRAINING_SET_PERCENTAGE, "-V"};
        remove.setOptions(optionsTrain);
        Instances train = Filter.useFilter(data, remove);
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
    public RemovePercentage getRemovePercentage(Instances data) throws Exception {
        // Randomizing
        data.randomize(new Random());

        // Filtering
        RemovePercentage remove = new RemovePercentage();
        remove.setInputFormat(data);
        return remove;
    }

    /**
     *
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

    // Change Model to be trained here!
    private void buildClassifier(Instances train) throws Exception {
        trainRF();
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
    public static Instances convertToSingleInstance(Instances instances, DataPoint dataPoint) {
        instances.delete();
        instances.setClassIndex(0);

        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
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

    public static void addInstances(ArrayList<DataPoint> dataPoints, Instances data) {
        double[] instanceValues = null;
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

            // rss mean
            instanceValues[index] = dataPoint.getRssData().getMean();
            index++;

            // rss values
            for (int i = 0; i < dataPoint.getRssData().getVariances().size(); i++) {
                instanceValues[index] = dataPoint.getRssData().getVariances().get(i);
                index++;
            }

            data.add(new DenseInstance(1.0, instanceValues));
        }
    }

    @NonNull
    public static ArrayList<Attribute> buildAttributes(ArrayList<DataPoint> dataPoints) {
        ArrayList<String> rooms = getRooms(dataPoints);

        // rooms + number of rss + number of sensors
        //int numberOfAttributes = 1 + CollectDataActivity.NUMBER_OF_ACCESS_POINTS + CollectDataActivity.NUMBER_OF_SENSORS;
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        // class: room
        attributes.add(new Attribute("room", rooms));

        assertion( dataPoints.get(0).getRssData().getValues().size() == dataPoints.get(0).getRssData().getVariances().size());

        // sensors
        attributes.add(new Attribute("magneticY", Attribute.NUMERIC));
        attributes.add(new Attribute("magneticZ", Attribute.NUMERIC));

        // rss values
        for (int i = 0; i < dataPoints.get(0).getRssData().getValues().size(); i++)
            attributes.add(new Attribute("rssValue" + i, Attribute.NUMERIC));

        // rss mean
        attributes.add(new Attribute("mean", Attribute.NUMERIC));

        // rss variances
        for (int i = 0; i < dataPoints.get(0).getRssData().getVariances().size(); i++)
            attributes.add(new Attribute("rssVariance" + i, Attribute.NUMERIC));

        return attributes;
    }

    @NonNull
    public static ArrayList<String> getRooms(ArrayList<DataPoint> dataPoints) {
        ArrayList<String> rooms = new ArrayList<String>();

        for (DataPoint dataPoint : dataPoints)
            if (!rooms.contains(dataPoint.getRoom()))
                rooms.add(dataPoint.getRoom());
        return rooms;
    }


    public void alert(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void assertion(boolean condition) {
        if (BuildConfig.DEBUG && !condition) throw new AssertionError();
    }
}
