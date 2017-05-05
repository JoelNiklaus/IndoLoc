package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;

/**
 *
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
    protected void fetchData() throws Exception {
        loadFiles("final_cds/train_room", "final_cds/test_room");
    }

    @Test
    public void excludeLight() throws Exception {
        train = WekaHelper.removeAttributes(train, "3");
        test = WekaHelper.removeAttributes(test, "3");
        conductPerformanceExperiment(train, test);
    }

    @Test
    public void excludeGravity() throws Exception {
        train = WekaHelper.removeAttributes(train, "6-8");
        test = WekaHelper.removeAttributes(test, "6-8");
        conductPerformanceExperiment(train, test);
    }

    @Test
    public void excludeMagnetic() throws Exception {
        train = WekaHelper.removeAttributes(train, "9-11");
        test = WekaHelper.removeAttributes(test, "9-11");
        conductPerformanceExperiment(train, test);
    }

    @Test
    public void excludeGravityMagnitude() throws Exception {
        train = WekaHelper.removeAttributes(train, "12");
        test = WekaHelper.removeAttributes(test, "12");
        conductPerformanceExperiment(train, test);
    }

    @Test
    public void excludeGeomagneticMagnitude() throws Exception {
        train = WekaHelper.removeAttributes(train, "13");
        test = WekaHelper.removeAttributes(test, "13");
        conductPerformanceExperiment(train, test);
    }

    @Test
    public void excludeMagneticProcessed() throws Exception {
        train = WekaHelper.removeAttributes(train, "14-15");
        test = WekaHelper.removeAttributes(test, "14-15");
        conductPerformanceExperiment(train, test);
    }


    @Test
    public void excludeGPS() throws Exception {
        train = WekaHelper.removeAttributes(train, "16-17");
        test = WekaHelper.removeAttributes(test, "16-17");
        conductPerformanceExperiment(train, test);
    }


    @Test
    public void excludeRSS() throws Exception {
        train = WekaHelper.removeAttributes(train, "18-27");
         test = WekaHelper.removeAttributes(test, "18-27");
        conductPerformanceExperiment(train, test);
    }


}