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
    public void roundLight() throws Exception {
        train = WekaHelper.roundAttribute(train, 2, 0.1);
        test = WekaHelper.roundAttribute(test, 2, 0.1);
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void roundGravity() throws Exception {
        train = WekaHelper.roundAttribute(train, 5, 0.2);
        test = WekaHelper.roundAttribute(test, 5, 0.2);
        train = WekaHelper.roundAttribute(train, 6, 0.2);
        test = WekaHelper.roundAttribute(test, 6, 0.2);
        train = WekaHelper.roundAttribute(train, 7, 0.2);
        test = WekaHelper.roundAttribute(test, 7, 0.2);
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void roundMagnetic() throws Exception {
        train = WekaHelper.roundAttribute(train, 8, 0.2);
        test = WekaHelper.roundAttribute(test, 8, 0.2);
        train = WekaHelper.roundAttribute(train, 9, 0.2);
        test = WekaHelper.roundAttribute(test, 9, 0.2);
        train = WekaHelper.roundAttribute(train, 10, 0.2);
        test = WekaHelper.roundAttribute(test, 10, 0.2);
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void roundGravityMagnitude() throws Exception {
        train = WekaHelper.roundAttribute(train, 11, 0.2);
        test = WekaHelper.roundAttribute(test, 11, 0.2);
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void roundGeomagneticMagnitude() throws Exception {
        train = WekaHelper.roundAttribute(train, 12, 0.2);
        test = WekaHelper.roundAttribute(test, 12, 0.2);
        conductPerformanceExperiment(train, test, false);
    }

    @Test
    public void roundMagneticProcessed() throws Exception {
        train = WekaHelper.roundAttribute(train, 13, 0.2);
        test = WekaHelper.roundAttribute(test, 13, 0.2);
        train = WekaHelper.roundAttribute(train, 14, 0.2);
        test = WekaHelper.roundAttribute(test, 14, 0.2);
        conductPerformanceExperiment(train, test, false);
    }

}