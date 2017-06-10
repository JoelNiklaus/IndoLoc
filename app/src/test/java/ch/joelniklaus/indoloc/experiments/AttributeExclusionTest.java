package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.helpers.WekaHelper;

// TODO find best simple method and add it to the meta classifiers.

// TODO Add confusion matrices to report
// TODO define groups:
/**
 * 1: Best number of access points ONLY WIFI
 * 2:
 * A: 7 WIFI
 * B: 7 WIFI, MagneticProcessed
 * C: 7 WIFI, MagneticProcessed, Latitude/Longitude
 * D: 7 WIFI, MagneticProcessed, GravityMagnitude
 */

/**
 * Tests the accuracy of the prediction when certain features are removed
 *
 * @author joelniklaus
 */
public class AttributeExclusionTest extends AbstractTest {

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
    public void OnlyWifi5AccessPoints() throws Exception {
        train = WekaHelper.removeAttributes(train, "23-27");
        test = WekaHelper.removeAttributes(test, "23-27");
        train = WekaHelper.removeAttributes(train, "2-17");
        test = WekaHelper.removeAttributes(test, "2-17");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void OnlyWifi6AccessPoints() throws Exception {
        train = WekaHelper.removeAttributes(train, "24-27");
        test = WekaHelper.removeAttributes(test, "24-27");
        train = WekaHelper.removeAttributes(train, "2-17");
        test = WekaHelper.removeAttributes(test, "2-17");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void OnlyWifi7AccessPoints() throws Exception {
        train = WekaHelper.removeAttributes(train, "25-27");
        test = WekaHelper.removeAttributes(test, "25-27");
        train = WekaHelper.removeAttributes(train, "2-17");
        test = WekaHelper.removeAttributes(test, "2-17");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void OnlyWifi8AccessPoints() throws Exception {
        train = WekaHelper.removeAttributes(train, "26-27");
        test = WekaHelper.removeAttributes(test, "26-27");
        train = WekaHelper.removeAttributes(train, "2-17");
        test = WekaHelper.removeAttributes(test, "2-17");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void OnlyWifi9AccessPoints() throws Exception {
        train = WekaHelper.removeAttributes(train, "27");
        test = WekaHelper.removeAttributes(test, "27");
        train = WekaHelper.removeAttributes(train, "2-17");
        test = WekaHelper.removeAttributes(test, "2-17");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }
    @Test
    public void OnlyWifi10AccessPoints() throws Exception {
        train = WekaHelper.removeAttributes(train, "2-17");
        test = WekaHelper.removeAttributes(test, "2-17");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void WifiAndMagneticProcessed() throws Exception {
        // wifi 8, 9, 10
        train = WekaHelper.removeAttributes(train, "25-27");
        test = WekaHelper.removeAttributes(test, "25-27");

        // latitude, longitude
        train = WekaHelper.removeAttributes(train, "16-17");
        test = WekaHelper.removeAttributes(test, "16-17");

        // everything before magneticProcessed
        train = WekaHelper.removeAttributes(train, "2-13");
        test = WekaHelper.removeAttributes(test, "2-13");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void WifiAndMagneticProcessedAndLatitudeLongitude() throws Exception {
        // wifi 8, 9, 10
        train = WekaHelper.removeAttributes(train, "25-27");
        test = WekaHelper.removeAttributes(test, "25-27");

        // everything before magneticProcessed
        train = WekaHelper.removeAttributes(train, "2-13");
        test = WekaHelper.removeAttributes(test, "2-13");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        conductPerformanceExperiment(train, test, true);
    }


    @Test
    public void WifiAndMagneticProcessedAndGravityMagnitudeGeomagneticMagnitude() throws Exception {
        // wifi 8, 9, 10
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

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void WifiAndMagneticProcessedAndLight() throws Exception {
        // wifi 8, 9, 10
        train = WekaHelper.removeAttributes(train, "25-27");
        test = WekaHelper.removeAttributes(test, "25-27");

        // latitude, longitude
        train = WekaHelper.removeAttributes(train, "16-17");
        test = WekaHelper.removeAttributes(test, "16-17");

        // everything before magneticProcessed but leave light
        train = WekaHelper.removeAttributes(train, "4-13");
        test = WekaHelper.removeAttributes(test, "4-13");

        train = WekaHelper.removeAttributes(train, "2");
        test = WekaHelper.removeAttributes(test, "2");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        System.out.println(test);

        conductPerformanceExperiment(train, test, true);
    }

    @Test
    public void WifiAndMagneticProcessedAndGravityMagneticRaw() throws Exception {
        // wifi 8, 9, 10
        train = WekaHelper.removeAttributes(train, "25-27");
        test = WekaHelper.removeAttributes(test, "25-27");

        // latitude, longitude
        train = WekaHelper.removeAttributes(train, "16-17");
        test = WekaHelper.removeAttributes(test, "16-17");

        // gravityMagnitude magneticMagnitude
        train = WekaHelper.removeAttributes(train, "12-13");
        test = WekaHelper.removeAttributes(test, "12-13");

        // everything before
        train = WekaHelper.removeAttributes(train, "2-5");
        test = WekaHelper.removeAttributes(test, "2-5");

        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);

        System.out.println(test);

        conductPerformanceExperiment(train, test, true);
    }
    @Test
    public void excludeLight() throws Exception {
        train = WekaHelper.removeAttributes(train, "3");
        test = WekaHelper.removeAttributes(test, "3");
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void excludeGravity() throws Exception {
        train = WekaHelper.removeAttributes(train, "6-8");
        test = WekaHelper.removeAttributes(test, "6-8");
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void excludeMagnetic() throws Exception {
        train = WekaHelper.removeAttributes(train, "9-11");
        test = WekaHelper.removeAttributes(test, "9-11");
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void excludeGravityMagnitude() throws Exception {
        train = WekaHelper.removeAttributes(train, "12");
        test = WekaHelper.removeAttributes(test, "12");
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void excludeGeomagneticMagnitude() throws Exception {
        train = WekaHelper.removeAttributes(train, "13");
        test = WekaHelper.removeAttributes(test, "13");
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void excludeMagneticProcessed() throws Exception {
        train = WekaHelper.removeAttributes(train, "14-15");
        test = WekaHelper.removeAttributes(test, "14-15");
        conductPerformanceExperiment(train, test, false);
    }


    @Test
    public void excludeGPS() throws Exception {
        train = WekaHelper.removeAttributes(train, "16-17");
        test = WekaHelper.removeAttributes(test, "16-17");
        conductPerformanceExperiment(train, test, false);
    }


    @Test
    public void excludeRSS() throws Exception {
        train = WekaHelper.removeAttributes(train, "18-27");
        test = WekaHelper.removeAttributes(test, "18-27");
        conductPerformanceExperiment(train, test, false);
    }


}