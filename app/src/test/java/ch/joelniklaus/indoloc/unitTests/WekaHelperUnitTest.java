package ch.joelniklaus.indoloc.unitTests;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.exceptions.DifferentHeaderException;
import ch.joelniklaus.indoloc.exceptions.InvalidRoomException;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.LocationData;
import ch.joelniklaus.indoloc.models.RSSData;
import ch.joelniklaus.indoloc.models.SensorData;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Comprises unit tests for all the functionality provided in the WekaHelper class.
 *
 * @author joelniklaus
 */
public class WekaHelperUnitTest {

    private final InstanceComparator comparator = new InstanceComparator();
    private FileHelper fileHelper = new FileHelper();

    protected Instances loadFile(String fileName) throws Exception, CouldNotLoadArffException {
        return fileHelper.loadArff(AbstractTest.ASSETS_PATH + fileName + AbstractTest.ENDING);
    }

    @Test
    public void testMergeInstances() throws Exception, DifferentHeaderException, CouldNotLoadArffException {
        Instances train = loadFile("unittests/train_landmark");
        Instances test = loadFile("unittests/test_landmark");

        Instances data = WekaHelper.mergeInstances(train, test);

        for (Instance instance : train)
            assertTrue(instancesContainInstance(data, instance));
        for (Instance instance : test)
            assertTrue(instancesContainInstance(data, instance));
    }

    @Test(expected = DifferentHeaderException.class)
    public void testMergeInstancesException() throws Exception, DifferentHeaderException, CouldNotLoadArffException {
        Instances train = loadFile("unittests/buildInstances");
        Instances test = loadFile("unittests/duplicates");

        Instances data = WekaHelper.mergeInstances(train, test);
    }

    @Test
    public void testRemoveOneAttribute() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/duplicates");

        // Remove third Attribute (index 2)
        Instances newData = WekaHelper.removeAttributes(data, "3");
        assertNotEquals(data, newData);
        assertEquals(data.numAttributes(), newData.numAttributes() + 1);
        assertTrue(data.attribute(0).equals(newData.attribute(0)));
        assertTrue(data.attribute(1).equals(newData.attribute(1)));
        assertFalse(data.attribute(2).equals(newData.attribute(2)));
        assertFalse(data.attribute(3).equals(newData.attribute(3)));
        assertTrue(data.attribute(3).equals(newData.attribute(2)));
        assertTrue(data.attribute(4).equals(newData.attribute(3)));

        assertEquals(data.numInstances(), newData.numInstances());
        assertEquals(data.instance(0).numValues(), newData.instance(0).numValues() + 1);

        System.out.println(data);
        System.out.println(newData);
    }

    @Test
    public void testRemoveMultipleAttributes() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/duplicates");

        // Remove third to fifth Attribute (indices 2 to 4)
        Instances newData = WekaHelper.removeAttributes(data, "3-5");
        assertNotEquals(data, newData);
        assertTrue(data.numAttributes() == newData.numAttributes() + 3);
        assertTrue(data.attribute(0).equals(newData.attribute(0)));
        assertTrue(data.attribute(1).equals(newData.attribute(1)));
        assertFalse(data.attribute(2).equals(newData.attribute(2)));
        assertFalse(data.attribute(3).equals(newData.attribute(3)));
        assertFalse(data.attribute(4).equals(newData.attribute(4)));
        assertTrue(data.attribute(5).equals(newData.attribute(2)));
        assertTrue(data.attribute(6).equals(newData.attribute(3)));

        assertEquals(data.numInstances(), newData.numInstances());
        assertEquals(data.instance(0).numValues(), newData.instance(0).numValues() + 3);
    }

    @Test
    public void testRemoveDuplicates() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/duplicates");

        Instances oldData = SerializationUtils.clone(data);
        assertEquals(9, data.numInstances());
        data = WekaHelper.removeDuplicates(data);
        assertEquals(9, oldData.numInstances());
        assertEquals(4, data.numInstances());

        assertTrue(oldData.numAttributes() == data.numAttributes());

        System.out.println(data.toString());
        System.out.println(oldData.toString());

        // Every instance of the small set should be in the large set
        for (int i = 0; i < data.numInstances(); i++)
            assertTrue(instancesContainInstance(oldData, data.instance(i)));
    }

    @Test
    public void testRoundAttribute() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/train_landmark");

        // round light to 0.1
        Instances newData = WekaHelper.roundAttribute(data, 2, 0.1);

        for (int i = 0; i < data.size(); i++)
            assertTrue(data.instance(i).value(2) - newData.instance(i).value(2) != 0);
        System.out.println(newData);
    }

    @Test
    public void testGetEvery2NdInstance() throws Exception, CouldNotLoadArffException {
        Instances train = loadFile("unittests/train_landmark");

        // reduce training set by 50%
        Instances newTrain = WekaHelper.getEveryNThInstance(train, 2);

        System.out.println(train);
        System.out.println(newTrain);

        for (int i = 0; i < train.size(); i += 2)
            assertTrue(instancesContainInstance(newTrain, train.instance(i)));
        for (int i = 1; i < train.size(); i += 2)
            assertFalse(instancesContainInstance(newTrain, train.instance(i)));
    }

    @Test
    public void testGetEvery3RdInstance() throws Exception, CouldNotLoadArffException {
        Instances train = loadFile("unittests/train_landmark");

        // reduce training set by 66%
        Instances newTrain = WekaHelper.getEveryNThInstance(train, 3);

        System.out.println(train);
        System.out.println(newTrain);

        for (int i = 0; i < train.size(); i += 3)
            assertTrue(instancesContainInstance(newTrain, train.instance(i)));
        for (int i = 1; i < train.size(); i += 3)
            assertFalse(instancesContainInstance(newTrain, train.instance(i)));
    }

    @Test
    public void testGetEvery4ThInstance() throws Exception, CouldNotLoadArffException {
        Instances train = loadFile("unittests/train_landmark");

        // reduce training set by 75%
        Instances newTrain = WekaHelper.getEveryNThInstance(train, 4);

        System.out.println(train);
        System.out.println(newTrain);

        for (int i = 0; i < train.size(); i += 4)
            assertTrue(instancesContainInstance(newTrain, train.instance(i)));
        for (int i = 1; i < train.size(); i += 4)
            assertFalse(instancesContainInstance(newTrain, train.instance(i)));
    }

    @Test
    public void testConvertToSingleInstance() throws Exception, InvalidRoomException, CouldNotLoadArffException {
        ArrayList<DataPoint> dataPoints = getDataPoints();
        DataPoint dataPoint = dataPoints.get(3);

        Instances expected = WekaHelper.buildInstances(dataPoints);
        expected.delete(0);
        expected.delete(0);
        expected.delete(0);
        expected.delete(1);
        Instances actual = loadFile("unittests/buildInstances");
        actual = WekaHelper.convertToSingleInstance(actual, dataPoint);
        testInstancesEqual(expected, actual);
    }

    @Test(expected = InvalidRoomException.class)
    public void testConvertToSingleInstanceException() throws Exception, InvalidRoomException, CouldNotLoadArffException {
        ArrayList<DataPoint> dataPoints = getDataPoints();
        DataPoint dataPoint = dataPoints.get(3);
        dataPoint.setRoom("hallo");

        Instances actual = loadFile("unittests/buildInstances");
        actual = WekaHelper.convertToSingleInstance(actual, dataPoint);
    }


/*
    @Test
    public void testGetTestingSetAndGetTrainingSetStratifiedRemoveFolds() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/remove");

        StratifiedRemoveFolds stratifiedRemoveFolds = WekaHelper.randomizeAndGetStratifiedRemoveFolds(data);
        Instances train = WekaHelper.getTrainingSet(data, stratifiedRemoveFolds);
        Instances test = WekaHelper.getTestingSet(data, stratifiedRemoveFolds);

        testTrainingAndTestingSet(data, train, test);
    }
*/

    @Test
    public void testGetTestingSetAndGetTrainingSetRemovePercentage() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/remove");

        RemovePercentage removePercentage = WekaHelper.randomizeAndGetRemovePercentage(data);
        Instances train = WekaHelper.getTrainingSet(data, removePercentage);
        Instances test = WekaHelper.getTestingSet(data, removePercentage);

        RandomForest rf = new RandomForest();
        rf.buildClassifier(train);
        System.out.println(test.firstInstance());
        System.out.println(Arrays.toString(rf.distributionForInstance(test.firstInstance())));
        System.out.println(test.lastInstance());
        System.out.println(Arrays.toString(rf.distributionForInstance(test.lastInstance())));

        testTrainingAndTestingSet(data, train, test);
    }

    @Test
    public void testGetTestingSetAndGetTrainingSetWekaStandard() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/remove");

        data.randomize(new java.util.Random());    // randomize instance order before splitting dataset
        Instances train = data.trainCV(2, 0);
        Instances test = data.testCV(2, 0);

        testTrainingAndTestingSet(data, train, test);
    }

    @Test
    public void testBuildInstances() throws Exception, CouldNotLoadArffException {
        ArrayList<DataPoint> dataPoints = getDataPoints();

        Instances expected = WekaHelper.buildInstances(dataPoints);
        Instances actual = loadFile("unittests/buildInstances");
        testInstancesEqual(expected, actual);
    }


    @Test
    public void testRemoveAllOfSpecificClass() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/duplicates");

        Instances oldData = SerializationUtils.clone(data);
        assertEquals(9, data.numInstances());
        data = WekaHelper.removeAllOfSpecificClass(data, 1);

        System.out.println(data.toString());
        System.out.println(oldData.toString());

        assertEquals(9, oldData.numInstances());
        assertEquals(4, data.numInstances());

        assertTrue(oldData.numAttributes() == data.numAttributes());

        // Every instance of the small set should be in the large set
        for (int i = 0; i < data.numInstances(); i++)
            assertTrue(instancesContainInstance(oldData, data.instance(i)));

        // No instance has class value 1
        for (int i = 0; i < data.numInstances(); i++)
            assertTrue((int) data.instance(i).classValue() != 1);
    }

    @Test
    public void testDistributionForInstance() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/remove");

        RemovePercentage removePercentage = WekaHelper.randomizeAndGetRemovePercentage(data);
        Instances train = WekaHelper.getTrainingSet(data, removePercentage);
        Instances test = WekaHelper.getTestingSet(data, removePercentage);

        RandomForest rf = new RandomForest();
        rf.buildClassifier(train);
        System.out.println(test.firstInstance());
        System.out.println(Arrays.toString(rf.distributionForInstance(test.firstInstance())));
        System.out.println(test.lastInstance());
        System.out.println(Arrays.toString(rf.distributionForInstance(test.lastInstance())));
    }

    @Test
    public void testClassificationDifference() throws Exception, CouldNotLoadArffException {
        Instances data = loadFile("unittests/remove");

        RemovePercentage removePercentage = WekaHelper.randomizeAndGetRemovePercentage(data);
        Instances train = WekaHelper.getTrainingSet(data, removePercentage);
        Instances test = WekaHelper.getTestingSet(data, removePercentage);

        Instance instance = test.firstInstance();

        RandomForest rf = new RandomForest();
        rf.buildClassifier(train);

        double expected = rf.classifyInstance(instance);

        for (int i = 0; i < 100; i++) {
            double actual = rf.classifyInstance(instance);
            assertEquals(expected, actual, 0.001);
            System.out.println(actual);
        }
    }

    private void testTrainingAndTestingSet(Instances data, Instances train, Instances test) {
        System.out.println(train.toString());
        System.out.println(test.toString());

        assertTrue(test.numInstances() < data.numInstances());
        //assertTrue(test.numInstances() < train.numInstances());
        assertTrue(train.numInstances() < data.numInstances());

        assertTrue(test.numAttributes() == train.numAttributes());
        assertTrue(train.numAttributes() == data.numAttributes());
        assertTrue(data.numAttributes() == test.numAttributes());


        //assertTrue(test.numInstances() <= (100 - Integer.parseInt(WekaHelper.TRAINING_SET_PERCENTAGE)) / 100.0 * data.numInstances() + 1);
        //assertTrue(train.numInstances() <= Integer.parseInt(WekaHelper.TRAINING_SET_PERCENTAGE) / 100.0 * data.numInstances() + 1);

        assertEquals(test.numInstances() + train.numInstances(), data.numInstances());

        // Every instance in the testing set has to be in the data set
        for (int i = 0; i < test.numInstances(); i++)
            assertTrue(instancesContainInstance(data, test.instance(i)));

        // Every instance in the training set has to be in the data set
        for (int i = 0; i < train.numInstances(); i++)
            assertTrue(instancesContainInstance(data, train.instance(i)));

        // No instance which is in the training set is in the testing set
        for (int i = 0; i < train.numInstances(); i++)
            assertFalse(instancesContainInstance(test, train.instance(i)));

        // No instance which is in the testing set is in the training set
        for (int i = 0; i < test.numInstances(); i++)
            assertFalse(instancesContainInstance(train, test.instance(i)));
    }


    @NonNull
    private ArrayList<DataPoint> getDataPoints() {
        ArrayList<DataPoint> dataPoints = new ArrayList<>();

        RSSData rssData1 = new RSSData(new ArrayList<>(Arrays.asList(0, 23, 39, 39, 39, 39, 39, 39, 0, 0)));
        float[] gravity1 = {8.1111f, 11.1111f, 8.111f};
        float[] magnetic1 = {3.41111f, 11.4111f, 6.16341f};
        SensorData sensorData1 = SensorData.getSensorDataTest(0, 121, 12, 0, gravity1, magnetic1, 23.23f, 32.122f, 56.677f, 5.96f);
        LocationData locationData1 = new LocationData(7.12094, 8.38923);
        dataPoints.add(new DataPoint("stube", sensorData1, rssData1, locationData1));

        RSSData rssData2 = new RSSData(new ArrayList<>(Arrays.asList(0, 23, 39, 39, 39, 39, 39, 39, 0, 0)));
        float[] gravity2 = {8.2222f, 22.2222f, 8.222f};
        float[] magnetic2 = {3.42222f, 22.4222f, 6.26342f};
        SensorData sensorData2 = SensorData.getSensorDataTest(0, 222, 22, 0, gravity2, magnetic2, 23.23f, 32.222f, 56.677f, 5.96f);
        LocationData locationData2 = new LocationData(7.22094, 8.38923);
        dataPoints.add(new DataPoint("kueche", sensorData2, rssData2, locationData2));

        RSSData rssData3 = new RSSData(new ArrayList<>(Arrays.asList(0, 23, 39, 39, 39, 39, 39, 39, 0, 0)));
        float[] gravity3 = {8.3232f, 32.3222f, 8.233f};
        float[] magnetic3 = {3.43232f, 22.4222f, 6.26343f};
        SensorData sensorData3 = SensorData.getSensorDataTest(0, 323, 32, 0, gravity3, magnetic3, 23.23f, 32.322f, 56.677f, 5.96f);
        LocationData locationData3 = new LocationData(7.32094, 8.38923);
        dataPoints.add(new DataPoint("badgross", sensorData3, rssData3, locationData3));

        RSSData rssData4 = new RSSData(new ArrayList<>(Arrays.asList(0, 23, 39, 39, 39, 39, 39, 39, 0, 0)));
        float[] gravity4 = {8.4242f, 42.4222f, 8.244f};
        float[] magnetic4 = {3.44242f, 22.4222f, 6.26344f};
        SensorData sensorData4 = SensorData.getSensorDataTest(0, 424, 42, 0, gravity4, magnetic4, 23.23f, 32.422f, 56.677f, 5.96f);
        LocationData locationData4 = new LocationData(7.42094, 8.38923);
        dataPoints.add(new DataPoint("badklein", sensorData4, rssData4, locationData4));

        RSSData rssData5 = new RSSData(new ArrayList<>(Arrays.asList(0, 23, 39, 39, 39, 39, 39, 39, 0, 0)));
        float[] gravity5 = {8.5252f, 52.5222f, 8.255f};
        float[] magnetic5 = {3.45252f, 22.4222f, 6.26345f};
        SensorData sensorData5 = SensorData.getSensorDataTest(0, 525, 52, 0, gravity5, magnetic5, 23.23f, 32.522f, 56.677f, 5.96f);
        LocationData locationData5 = new LocationData(7.52094, 8.38923);
        dataPoints.add(new DataPoint("gang", sensorData5, rssData5, locationData5));

        return dataPoints;
    }


    private boolean instancesContainInstance(Instances haystick, Instance needle) {
        for (Instance instance : haystick)
            if (areInstancesEqual(instance, needle))
                return true;
        return false;
    }

    private boolean areInstancesEqual(Instance first, Instance second) {
        return comparator.compare(first, second) == 0;
    }

    private void testInstancesEqual(Instances expected, Instances actual) {
        assertTrue(actual.equalHeaders(expected));
        for (int i = 0; i < expected.numInstances(); i++)
            assertTrue(areInstancesEqual(expected.instance(i), actual.instance(i)));
    }

}
