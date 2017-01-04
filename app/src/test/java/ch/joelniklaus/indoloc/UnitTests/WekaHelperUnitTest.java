package ch.joelniklaus.indoloc.unitTests;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.RSSData;
import ch.joelniklaus.indoloc.models.SensorData;
import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.filters.supervised.instance.StratifiedRemoveFolds;
import weka.filters.unsupervised.instance.RemovePercentage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WekaHelperUnitTest extends AbstractTest {

    private InstanceComparator comparator = new InstanceComparator();

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGetTestingSetAndGetTrainingSetStratifiedRemoveFolds() throws Exception {
        Instances data = loadFile("unittests/remove");

        StratifiedRemoveFolds stratifiedRemoveFolds = wekaHelper.randomizeAndGetStratifiedRemoveFolds(data);
        Instances train = wekaHelper.getTrainingSet(data, stratifiedRemoveFolds);
        Instances test = wekaHelper.getTestingSet(data, stratifiedRemoveFolds);

        testTrainingAndTestingSet(data, train, test);
    }

    @Test
    public void testGetTestingSetAndGetTrainingSetRemovePercentage() throws Exception {
        Instances data = loadFile("unittests/remove");

        RemovePercentage removePercentage = wekaHelper.randomizeAndGetRemovePercentage(data);
        Instances train = wekaHelper.getTrainingSet(data, removePercentage);
        Instances test = wekaHelper.getTestingSet(data, removePercentage);

        testTrainingAndTestingSet(data, train, test);
    }


    @Test
    public void testGetTestingSetAndGetTrainingSetWekaStandard() throws Exception {
        Instances data = loadFile("unittests/remove");

        data.randomize(new java.util.Random());    // randomize instance order before splitting dataset
        Instances train = data.trainCV(2, 0);
        Instances test = data.testCV(2, 0);

        testTrainingAndTestingSet(data, train, test);
    }

    @Test
    public void testBuildInstances() throws Exception {
        ArrayList<DataPoint> dataPoints = getDataPoints();

        Instances expected = wekaHelper.buildInstances(dataPoints);
        Instances actual = loadFile("unittests/buildInstances");
        testInstancesEqual(expected, actual);
    }


    @Test
    public void testConvertToSingleInstance() throws Exception {
        ArrayList<DataPoint> dataPoints = getDataPoints();
        DataPoint dataPoint = dataPoints.get(3);

        Instances expected = wekaHelper.buildInstances(dataPoints);
        expected.delete(0);
        expected.delete(0);
        expected.delete(0);
        expected.delete(1);
        Instances actual = loadFile("unittests/buildInstances");
        wekaHelper.convertToSingleInstance(actual, dataPoint);
        testInstancesEqual(expected, actual);
    }

    @Test
    public void testRemoveOneAttribute() throws Exception {
        Instances data = loadFile("unittests/duplicates");

        // Remove third Attribute (index 2)
        Instances newData = wekaHelper.removeAttributes(data, "3");
        assertNotEquals(data, newData);
        assertTrue(data.numAttributes() == newData.numAttributes() + 1);
        assertTrue(data.attribute(0).equals(newData.attribute(0)));
        assertTrue(data.attribute(1).equals(newData.attribute(1)));
        assertFalse(data.attribute(2).equals(newData.attribute(2)));
        assertFalse(data.attribute(3).equals(newData.attribute(3)));
        assertTrue(data.attribute(3).equals(newData.attribute(2)));
        assertTrue(data.attribute(4).equals(newData.attribute(3)));
    }

    @Test
    public void testRemoveMultipleAttributes() throws Exception {
        Instances data = loadFile("unittests/duplicates");

        // Remove third to fifth Attribute (indices 2 to 4)
        Instances newData = wekaHelper.removeAttributes(data, "3-5");
        assertNotEquals(data, newData);
        assertTrue(data.numAttributes() == newData.numAttributes() + 3);
        assertTrue(data.attribute(0).equals(newData.attribute(0)));
        assertTrue(data.attribute(1).equals(newData.attribute(1)));
        assertFalse(data.attribute(2).equals(newData.attribute(2)));
        assertFalse(data.attribute(3).equals(newData.attribute(3)));
        assertFalse(data.attribute(4).equals(newData.attribute(4)));
        assertTrue(data.attribute(5).equals(newData.attribute(2)));
        assertTrue(data.attribute(6).equals(newData.attribute(3)));
    }

    @Test
    public void testRemoveDuplicates() throws Exception {
        Instances data = loadFile("unittests/duplicates");

        Instances oldData = SerializationUtils.clone(data);
        assertEquals(9,data.numInstances());
        data = wekaHelper.removeDuplicates(data);
        assertEquals(9,oldData.numInstances());
        assertEquals(4,data.numInstances());

        assertTrue(oldData.numAttributes() == data.numAttributes());

        System.out.println(data.toString());
        System.out.println(oldData.toString());

        // Every instance of the small set should be in the large set
        for (int i = 0; i < data.numInstances(); i++)
            assertTrue(dataContainsInstance(oldData, data.instance(i)));
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
            assertTrue(dataContainsInstance(data, test.instance(i)));

        // Every instance in the training set has to be in the data set
        for (int i = 0; i < train.numInstances(); i++)
            assertTrue(dataContainsInstance(data, train.instance(i)));

        // No instance which is in the training set is in the testing set
        for (int i = 0; i < train.numInstances(); i++)
            assertFalse(dataContainsInstance(test, train.instance(i)));

        // No instance which is in the testing set is in the training set
        for (int i = 0; i < test.numInstances(); i++)
            assertFalse(dataContainsInstance(train, test.instance(i)));
    }

    private void addDataPoint(ArrayList<DataPoint> dataPoints, String room, SensorData sensorData, Integer[] rss) {
        DataPoint dataPoint = getDataPoint(room, sensorData, rss);
        dataPoints.add(dataPoint);
    }

    @NonNull
    private DataPoint getDataPoint(String room, SensorData sensorData, Integer[] rss) {
        ArrayList<Integer> rssList = new ArrayList<>();
        for (Integer rssValue : rss)
            rssList.add(rssValue);
        return new DataPoint(room, sensorData, RSSData.createRSSDataTest(rssList));
    }


    private boolean dataContainsInstance(Instances data, Instance instance) {
        for (int i = 0; i < data.numInstances(); i++)
            if (comparator.compare(instance, data.instance(i)) == 0)
                return true;
        return false;
    }

    @NonNull
    private ArrayList<DataPoint> getDataPoints() {
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        Integer[] rss1 = {0, 23, 39, 39, 39, 39, 39, 39};
        addDataPoint(dataPoints, "stube", new SensorData(17.8, -39.8), rss1);
        Integer[] rss2 = {0, 10, 31, 31, 31, 31, 31, 31};
        addDataPoint(dataPoints, "kueche", new SensorData(8.1, -38.7), rss2);
        Integer[] rss3 = {0, 16, 16, 23, 23, 23, 14, 14};
        addDataPoint(dataPoints, "badgross", new SensorData(4.4, -42.3), rss3);
        Integer[] rss4 = {0, 20, 33, 33, 33, 33, 33, 33};
        addDataPoint(dataPoints, "badklein", new SensorData(5.5, -44.1), rss4);
        Integer[] rss5 = {0, 11, 34, 34, 34, 34, 34, 34};
        addDataPoint(dataPoints, "gang", new SensorData(11.2, -39.1), rss5);
        return dataPoints;
    }

    private void testInstancesEqual(Instances expected, Instances actual) {
        assertTrue(actual.equalHeaders(expected));
        for (int i = 0; i < expected.numInstances(); i++)
            assertTrue(comparator.compare(expected.instance(i), actual.instance(i)) == 0);
    }

}
