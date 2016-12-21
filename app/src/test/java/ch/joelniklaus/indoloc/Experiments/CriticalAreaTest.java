package ch.joelniklaus.indoloc.Experiments;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.ClassifierRating;
import weka.core.Instances;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CriticalAreaTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        setFile("critical_area.arff");
        super.setUp();
    }

    @Test
    public void testCriticalArea() throws Exception {
        ArrayList<ClassifierRating> classifierRatings = getClassifierRatings(data);
        sortAndPrintClassifierRatings(classifierRatings);
    }

    @Test
    public void testCriticalAreaWithoutDuplicates() throws Exception {
        data = wekaHelper.removeDuplicates(data);
        ArrayList<ClassifierRating> classifierRatings = getClassifierRatings(data);
        sortAndPrintClassifierRatings(classifierRatings);
    }

    /**
     * Tests if using the magnetic field values, mean and variances as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesMeanVariancesImprovement() throws Exception {
        Instances with = wekaHelper.removeDuplicates(data);
        Instances without = wekaHelper.removeAttributes(data, "12-20");
        without = wekaHelper.removeAttributes(without, "2-3");

        testWithAndWithout(with, without);
    }
}