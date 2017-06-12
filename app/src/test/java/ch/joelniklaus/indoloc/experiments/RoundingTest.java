package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.helpers.WekaHelper;

/**
 * Tests if rounding of certain features improve the accuracy.
 *
 * @author joelniklaus
 */
public class RoundingTest extends AbstractTest {

    /* Attribute Indices:
     * 1 -> Class Attribute: Room/Landmark (DO NOT REMOVE THIS ONE!)
     * 2 -> ambientTemperature (0 because sensor not available in test device)
     * 3 -> light
     * 4 -> pressure (0 because sensor not available in test device)
     * 5 -> relativeHumidity (0 because sensor not available in test device)
     * 6 -> gravityX (raw value in device coordinate system)
     * 7 -> gravityY (raw value in device coordinate system)
     * 8 -> gravityZ (raw value in device coordinate system)
     * 9 -> magneticX (raw value in device coordinate system)
     * 10 -> magneticY (raw value in device coordinate system)
     * 11 -> magneticZ (raw value in device coordinate system)
     * 12 -> gravityMagnitude (computed z value in global coordinate system)
     * 13 -> geomagneticMagnitude (computed y value in global coordinate system)
     * 14 -> magneticProcessedY
     * 15 -> magneticProcessedZ
     * 16 -> latitude (gps)
     * 17 -> longitude (gps)
     * 18 -> rssValue0
     * 19 -> rssValue1
     * 20 -> rssValue2
     * 21 -> rssValue3
     * 22 -> rssValue4
     * 23 -> rssValue5
     * 24 -> rssValue6
     * 25 -> rssValue7
     * 26 -> rssValue8
     * 27 -> rssValue9
     */


    @Override
    protected void fetchData() throws Exception, CouldNotLoadArffException {
        loadFiles("thesis/bern/room/train", "thesis/bern/room/test");
    }

    @Test
    public void roundReducedDatasetToInteger() throws Exception {
        reduceDatasets();

        conductPerformanceExperiment(train, test, true);

        float round = 1;
        train = WekaHelper.roundAttribute(train, 1, round);
        test = WekaHelper.roundAttribute(test, 1, round);
        train = WekaHelper.roundAttribute(train, 2, round);
        test = WekaHelper.roundAttribute(test, 2, round);
        train = WekaHelper.roundAttribute(train, 3, round);
        test = WekaHelper.roundAttribute(test, 3, round);
        train = WekaHelper.roundAttribute(train, 4, round);
        test = WekaHelper.roundAttribute(test, 4, round);


        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        System.out.println(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void roundReducedDatasetToOneFifth() throws Exception {
        reduceDatasets();

        conductPerformanceExperiment(train, test, true);

        float round = 0.2f;
        train = WekaHelper.roundAttribute(train, 1, round);
        test = WekaHelper.roundAttribute(test, 1, round);
        train = WekaHelper.roundAttribute(train, 2, round);
        test = WekaHelper.roundAttribute(test, 2, round);
        train = WekaHelper.roundAttribute(train, 3, round);
        test = WekaHelper.roundAttribute(test, 3, round);
        train = WekaHelper.roundAttribute(train, 4, round);
        test = WekaHelper.roundAttribute(test, 4, round);


        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        System.out.println(test);

        conductPerformanceExperiment(train, test, true);
    }
    @Test
    public void roundReducedDatasetToOneTenth() throws Exception {
        reduceDatasets();

        conductPerformanceExperiment(train, test, true);

        float round = 0.1f;
        train = WekaHelper.roundAttribute(train, 1, round);
        test = WekaHelper.roundAttribute(test, 1, round);
        train = WekaHelper.roundAttribute(train, 2, round);
        test = WekaHelper.roundAttribute(test, 2, round);
        train = WekaHelper.roundAttribute(train, 3, round);
        test = WekaHelper.roundAttribute(test, 3, round);
        train = WekaHelper.roundAttribute(train, 4, round);
        test = WekaHelper.roundAttribute(test, 4, round);


        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        System.out.println(test);

        conductPerformanceExperiment(train, test, true);
    }



    @Test
    public void roundLight() throws Exception {
        train = WekaHelper.roundAttribute(train, 2, 1);
        test = WekaHelper.roundAttribute(test, 2, 1);
        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void roundGravity() throws Exception {
        train = WekaHelper.roundAttribute(train, 5, 0.2);
        test = WekaHelper.roundAttribute(test, 5, 0.2);
        train = WekaHelper.roundAttribute(train, 6, 0.2);
        test = WekaHelper.roundAttribute(test, 6, 0.2);
        train = WekaHelper.roundAttribute(train, 7, 0.2);
        test = WekaHelper.roundAttribute(test, 7, 0.2);
        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void roundMagnetic() throws Exception {
        train = WekaHelper.roundAttribute(train, 8, 0.2);
        test = WekaHelper.roundAttribute(test, 8, 0.2);
        train = WekaHelper.roundAttribute(train, 9, 0.2);
        test = WekaHelper.roundAttribute(test, 9, 0.2);
        train = WekaHelper.roundAttribute(train, 10, 0.2);
        test = WekaHelper.roundAttribute(test, 10, 0.2);
        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void roundGravityMagnitude() throws Exception {
        train = WekaHelper.roundAttribute(train, 11, 0.2);
        test = WekaHelper.roundAttribute(test, 11, 0.2);
        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void roundGeomagneticMagnitude() throws Exception {
        train = WekaHelper.roundAttribute(train, 12, 0.2);
        test = WekaHelper.roundAttribute(test, 12, 0.2);
        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void roundMagneticProcessed() throws Exception {
        train = WekaHelper.roundAttribute(train, 13, 1);
        test = WekaHelper.roundAttribute(test, 13, 1);
        train = WekaHelper.roundAttribute(train, 14, 1);
        test = WekaHelper.roundAttribute(test, 14, 1);
        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void roundEverything() throws Exception {
        train = WekaHelper.roundAttribute(train, 2, 1);
        test = WekaHelper.roundAttribute(test, 2, 1);

        train = WekaHelper.roundAttribute(train, 5, 1);
        test = WekaHelper.roundAttribute(test, 5, 1);
        train = WekaHelper.roundAttribute(train, 6, 1);
        test = WekaHelper.roundAttribute(test, 6, 1);
        train = WekaHelper.roundAttribute(train, 7, 1);
        test = WekaHelper.roundAttribute(test, 7, 1);

        train = WekaHelper.roundAttribute(train, 8, 1);
        test = WekaHelper.roundAttribute(test, 8, 1);
        train = WekaHelper.roundAttribute(train, 9, 1);
        test = WekaHelper.roundAttribute(test, 9, 1);
        train = WekaHelper.roundAttribute(train, 10, 1);
        test = WekaHelper.roundAttribute(test, 10, 1);

        train = WekaHelper.roundAttribute(train, 11, 1);
        test = WekaHelper.roundAttribute(test, 11, 1);

        train = WekaHelper.roundAttribute(train, 12, 1);
        test = WekaHelper.roundAttribute(test, 12, 1);

        train = WekaHelper.roundAttribute(train, 13, 1);
        test = WekaHelper.roundAttribute(test, 13, 1);
        train = WekaHelper.roundAttribute(train, 14, 1);
        test = WekaHelper.roundAttribute(test, 14, 1);

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }

    /**
     * Reduces the dataset to the first 7 rss values, magneticProcessed, gravityMagnitude and geomagneticMagnitude
     * @throws Exception
     */
    public void reduceDatasets() throws Exception {
        // RSS 8, 9, 10
        train = WekaHelper.removeAttributes(train, "25-27");
        test = WekaHelper.removeAttributes(test, "25-27");

        // latitude, longitude
        train = WekaHelper.removeAttributes(train, "16-17");
        test = WekaHelper.removeAttributes(test, "16-17");

        // everything before gravityMagnitude
        train = WekaHelper.removeAttributes(train, "2-11");
        test = WekaHelper.removeAttributes(test, "2-11");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);
    }

}