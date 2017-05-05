package ch.joelniklaus.indoloc.unitTests;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.DifferentHeaderException;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WekaHelperUnitTest {

    private final InstanceComparator comparator = new InstanceComparator();
    private FileHelper fileHelper = new FileHelper();

    protected Instances loadFile(String fileName) throws Exception {
        return fileHelper.loadArff(AbstractTest.ASSETS_PATH + fileName + AbstractTest.ENDING);
    }

    @Test
    public void testMergeInstances() throws Exception, DifferentHeaderException {
        Instances train = loadFile("unittests/train_landmark");
        Instances test = loadFile("unittests/test_landmark");

        Instances data = WekaHelper.mergeInstances(train, test);

        for (Instance instance : train)
            assertTrue(instancesContainInstance(data, instance));
        for (Instance instance : test)
            assertTrue(instancesContainInstance(data, instance));
    }

    @Test(expected = DifferentHeaderException.class)
    public void testMergeInstancesException() throws Exception, DifferentHeaderException {
        Instances train = loadFile("final_cds/train_landmark");
        Instances test = loadFile("final_cds/test_room");

        Instances data = WekaHelper.mergeInstances(train, test);
    }

    @Test
    public void testRemoveOneAttribute() throws Exception {
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
    public void testRemoveMultipleAttributes() throws Exception {
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
    public void testRemoveDuplicates() throws Exception {
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
    public void testRoundAttribute() throws Exception {
        Instances data = loadFile("unittests/train_landmark");

        // round light to 0.1
        Instances newData = WekaHelper.roundAttribute(data, 2, 0.1);

        for (int i = 0; i < data.size(); i++)
            assertTrue(data.instance(i).value(2) - newData.instance(i).value(2) != 0);
        System.out.println(newData);
    }

    @Test
    public void testGetEvery2NdInstance() throws Exception {
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
    public void testGetEvery3RdInstance() throws Exception {
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
    public void testGetEvery4ThInstance() throws Exception {
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




    /*
    @Test
    public void testGetTestingSetAndGetTrainingSetStratifiedRemoveFolds() throws Exception {
        Instances data = loadFile("unittests/remove");

        StratifiedRemoveFolds stratifiedRemoveFolds = WekaHelper.randomizeAndGetStratifiedRemoveFolds(data);
        Instances train = WekaHelper.getTrainingSet(data, stratifiedRemoveFolds);
        Instances test = WekaHelper.getTestingSet(data, stratifiedRemoveFolds);

        testTrainingAndTestingSet(data, train, test);
    }

    @Test
    public void testGetTestingSetAndGetTrainingSetRemovePercentage() throws Exception {
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

        Instances expected = WekaHelper.buildInstances(dataPoints);
        Instances actual = loadFile("unittests/buildInstances");
        testInstancesEqual(expected, actual);
    }


    @Test
    public void testConvertToSingleInstance() throws Exception {
        ArrayList<DataPoint> dataPoints = getDataPoints();
        DataPoint dataPoint = dataPoints.get(3);

        Instances expected = WekaHelper.buildInstances(dataPoints);
        expected.delete(0);
        expected.delete(0);
        expected.delete(0);
        expected.delete(1);
        Instances actual = loadFile("unittests/buildInstances");
        WekaHelper.convertToSingleInstance(actual, dataPoint);
        testInstancesEqual(expected, actual);
    }



    @Test
    public void testRemoveAllOfSpecificClass() throws Exception {
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
            assertTrue(dataContainsInstance(oldData, data.instance(i)));

        // No instance has class value 1
        for (int i = 0; i < data.numInstances(); i++)
            assertTrue((int) data.instance(i).classValue() != 1);
    }

    @Test
    public void testDistributionForInstance() throws Exception {
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
    public void testClassificationDifference() throws Exception {
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
        addDataPoint(dataPoints, "stube", new SensorData(17, -39), rss1);
        Integer[] rss2 = {0, 10, 31, 31, 31, 31, 31, 31};
        addDataPoint(dataPoints, "kueche", new SensorData(8, -38), rss2);
        Integer[] rss3 = {0, 16, 16, 23, 23, 23, 14, 14};
        addDataPoint(dataPoints, "badgross", new SensorData(4, -42), rss3);
        Integer[] rss4 = {0, 20, 33, 33, 33, 33, 33, 33};
        addDataPoint(dataPoints, "badklein", new SensorData(5, -44), rss4);
        Integer[] rss5 = {0, 11, 34, 34, 34, 34, 34, 34};
        addDataPoint(dataPoints, "gang", new SensorData(11, -39), rss5);
        return dataPoints;
    }
    */

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
