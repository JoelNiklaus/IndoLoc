package ch.joelniklaus.indoloc.experiments;

import org.junit.Before;
import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.core.Instances;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExperimentTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /* Attribute Indices:
     * 1 -> Class Attribute: Room
     * 2 - 3 -> Magnetic Field
     * 4 - 11 -> RSSI Values
     * 12 -> RSS Mean
     * 13 - 20 RSS Variances
     */


    /**
     * Tests if using the magnetic field values as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesImprovement() throws Exception {
        Instances data = loadFile("experiments/experiment_new");

        Instances with = wekaHelper.removeDuplicates(data);
        Instances without = wekaHelper.removeAttributes(with, "2-3");

        testWithAndWithout(with, without);
    }

    /**
     * Tests if using the magnetic field values as features really improves the accuracy
     * Separately collected sets
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesImprovementSeparate() throws Exception {
        Instances train = loadFile("cds/train");
        Instances test = loadFile("cds/test");

        Instances withTrain = wekaHelper.removeDuplicates(train);
        Instances withoutTrain = wekaHelper.removeAttributes(withTrain, "2-3");

        Instances withTest = wekaHelper.removeDuplicates(test);
        Instances withoutTest = wekaHelper.removeAttributes(withTest, "2-3");

        testWithAndWithout(withTrain, withTest, withoutTrain, withoutTest);
    }

    /**
     * Tests if using the mean as a feature really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMeanImprovement() throws Exception {
        Instances data = loadFile("experiments/experiment_new");

        Instances with = WekaHelper.removeDuplicates(data);
        Instances without = WekaHelper.removeAttributes(with, "12");

        testWithAndWithout(with, without);
    }

    /**
     * Tests if using the mean as a feature really improves the accuracy
     * Separately collected sets
     *
     * @throws Exception
     */
    @Test
    public void testMeanImprovementSeparate() throws Exception {
        Instances train = loadFile("experiments/train");
        Instances test = loadFile("experiments/test");

        Instances withTrain = wekaHelper.removeDuplicates(train);
        Instances withoutTrain = wekaHelper.removeAttributes(withTrain, "12");

        Instances withTest = wekaHelper.removeDuplicates(test);
        Instances withoutTest = wekaHelper.removeAttributes(withTest, "12");

        testWithAndWithout(withTrain, withTest, withoutTrain, withoutTest);
    }

    /**
     * Tests if using the variances as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testVariancesImprovement() throws Exception {
        Instances data = loadFile("experiments/experiment_new");

        Instances with = WekaHelper.removeDuplicates(data);
        Instances without = WekaHelper.removeAttributes(with, "13-20");

        testWithAndWithout(with, without);
    }

    /**
     * Tests if using the variances as features really improves the accuracy
     * Separately collected sets
     *
     * @throws Exception
     */
    @Test
    public void testVariancesImprovementSeparate() throws Exception {
        Instances train = loadFile("experiments/train");
        Instances test = loadFile("experiments/test");

        Instances withTrain = wekaHelper.removeDuplicates(train);
        Instances withoutTrain = wekaHelper.removeAttributes(withTrain, "13-20");

        Instances withTest = wekaHelper.removeDuplicates(test);
        Instances withoutTest = wekaHelper.removeAttributes(withTest, "13-20");

        testWithAndWithout(withTrain, withTest, withoutTrain, withoutTest);
    }

    /**
     * Tests if using the magnetic field values, mean and variances as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesMeanVariancesImprovement() throws Exception {
        Instances data = loadFile("experiments/experiment_new");

        Instances with = WekaHelper.removeDuplicates(data);
        Instances without = WekaHelper.removeAttributes(with, "12-20");
        without = WekaHelper.removeAttributes(without, "2-3");

        testWithAndWithout(with, without);
    }

    /**
     * Tests if using the magnetic field values, mean and variances as features really improves the accuracy
     * Separately collected sets
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesMeanVariancesImprovementSeparate() throws Exception {
        Instances train = loadFile("cds/train");
        Instances test = loadFile("cds/test");

        Instances withTrain = wekaHelper.removeDuplicates(train);
        Instances withoutTrain = wekaHelper.removeAttributes(withTrain, "12-20");
        withoutTrain = wekaHelper.removeAttributes(withoutTrain, "2-3");

        Instances withTest = wekaHelper.removeDuplicates(test);
        Instances withoutTest = wekaHelper.removeAttributes(withTest, "12-20");
        withoutTest = wekaHelper.removeAttributes(withoutTest, "2-3");


        testWithAndWithout(withTrain, withTest, withoutTrain, withoutTest);
    }

}