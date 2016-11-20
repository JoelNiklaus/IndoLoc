package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import ch.joelniklaus.indoloc.LibSVM;
import ch.joelniklaus.indoloc.activities.CollectDataActivity;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.SensorsValue;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Bagging;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

// Weka version = 3.7.3
// weka_old.jar

public class WekaHelper {

    private Context context;

    private Classifier classifier;

    public static final String TRAINING_SET_PERCENTAGE = "80";

    private Timer timer = new Timer();

    public WekaHelper() {

    }

    public WekaHelper(Context context) {
        this.context = context;
    }

    public Evaluation evaluate(Instances data, Classifier classifier) throws Exception {
        timer.reset();
        RemovePercentage remove = getRemovePercentage(data);

        Instances train = getTrainingSet(data, remove);

        Instances test = getTestingSet(data, remove);

        classifier.buildClassifier(train);

        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(classifier, test);

        //alert(evaluation.toSummaryString("Time: " + timer.timeElapsed() + "ms\n\nResults\n======\n", false));

        return evaluation;
    }

    public void test(Instances test) throws Exception {
        timer.reset();
        String results = "";
        for (int i = 0; i < test.numInstances(); i++) {
            double actualClass = test.instance(i).classValue();
            String actual = test.classAttribute().value((int) actualClass);

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
    public Instances train(Instances data) throws Exception {
        timer.reset();
        RemovePercentage remove = getRemovePercentage(data);

        Instances train = getTrainingSet(data, remove);

        Instances test = getTestingSet(data, remove);

        buildClassifier(train);

        alert("Time: " + timer.timeElapsed() + "ms\n\nModel successfully trained: \n" + classifier.toString());
        return test;
    }

    @NonNull
    private Instances getTestingSet(Instances data, RemovePercentage remove) throws Exception {
        String[] optionsTest = {"-P", TRAINING_SET_PERCENTAGE};
        remove.setOptions(optionsTest);
        Instances test = Filter.useFilter(data, remove);
        test.setClassIndex(0);
        return test;
    }

    @NonNull
    private Instances getTrainingSet(Instances data, RemovePercentage remove) throws Exception {
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
    private RemovePercentage getRemovePercentage(Instances data) throws Exception {
        // Randomizing
        data.randomize(new Random());

        // Filtering
        RemovePercentage remove = new RemovePercentage();
        remove.setInputFormat(data);
        return remove;
    }

    // Change Model to be trained here!
    private void buildClassifier(Instances train) throws Exception {
        trainLR();
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


    @NonNull
    public static Instances convertToSingleInstance(Instances instances, DataPoint dataPoint) {
        instances.delete();

        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
        dataPoints.add(dataPoint);
        addInstances(dataPoints, instances);
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

            // room
            instanceValues[0] = data.classAttribute().indexOfValue(dataPoint.getRoom());

            // rss
            ArrayList<Integer> rssListTemp = dataPoint.getRssList();

            for (int i = 0; i < rssListTemp.size(); i++)
                instanceValues[1 + i] = rssListTemp.get(i);

            // sensors
            SensorsValue sensors = dataPoint.getSensors();
            //instanceValues[rssListTemp.size() + 1] = sensors.getAmbientTemperature();
            //instanceValues[rssListTemp.size() + 4] = sensors.getRelativeHumidity();
            instanceValues[rssListTemp.size() + 1] = sensors.getLight();
            instanceValues[rssListTemp.size() + 2] = sensors.getPressure();

            data.add(new DenseInstance(1.0, instanceValues));
        }
    }

    @NonNull
    public static ArrayList<Attribute> buildAttributes(ArrayList<DataPoint> dataPoints) {
        ArrayList<String> rooms = getRooms(dataPoints);

        // rooms + number of rss + number of sensors
        int numberOfAttributes = 1 + CollectDataActivity.NUMBER_OF_ACCESS_POINTS + CollectDataActivity.NUMBER_OF_SENSORS;
        ArrayList<Attribute> attributes = new ArrayList<Attribute>(numberOfAttributes);

        // class: room
        attributes.add(new Attribute("room", rooms));

        // rss
        for (int i = 0; i < dataPoints.get(0).getRssList().size(); i++)
            attributes.add(new Attribute("rss" + i, Attribute.NUMERIC));

        // sensors
        //attributes.add(new Attribute("ambient_temperature", Attribute.NUMERIC));
        //attributes.add(new Attribute("relative_humidity", Attribute.NUMERIC));
        attributes.add(new Attribute("light", Attribute.NUMERIC));
        attributes.add(new Attribute("pressure", Attribute.NUMERIC));
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
}
