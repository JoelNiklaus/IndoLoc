package ch.joelniklaus.indoloc.UnitTests;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import weka.core.Instances;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WekaHelperUnitTest extends AbstractTest {
    @Before
    public void setUp() throws Exception {
        setFile("test/duplicates.arff");
        super.setUp();
    }

    @Test
    public void testRemoveOneAttribute() throws Exception {
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
        Instances oldData = SerializationUtils.clone(data);
        assertTrue(data.numInstances() == 9);
        data = wekaHelper.removeDuplicates(data);
        assertTrue(oldData.numInstances() == 9);
        assertTrue(data.numInstances() == 5);

        assertTrue(oldData.numAttributes() == data.numAttributes());

        System.out.println(data.toString());
        System.out.println(oldData.toString());

        // Every instance of the small set should be in the large set
        for (int i = 0; i < data.numInstances(); i++)
            assertTrue(oldData.contains(data.instance(i)));
    }
}
