package ch.joelniklaus.indoloc;

import org.junit.Before;
import org.junit.Test;

import weka.core.Instances;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WekaHelperUnitTest extends AbstractTest{
    @Before
    public void setUp() throws Exception{
        super.setUp();
    }

    @Test
    public void testRemoveAttribute() throws Exception {
        // Remove third Attribute (index 2)
        Instances newData = wekaHelper.removeAttribute(data, "3");
        assertNotEquals(data, newData);
        assertTrue(data.numAttributes() == newData.numAttributes() + 1);
        assertTrue(data.attribute(0).equals(newData.attribute(0)));
        assertTrue(data.attribute(1).equals(newData.attribute(1)));
        assertFalse(data.attribute(2).equals(newData.attribute(2)));
        assertFalse(data.attribute(3).equals(newData.attribute(3)));
        assertTrue(data.attribute(3).equals(newData.attribute(2)));
        assertTrue(data.attribute(4).equals(newData.attribute(3)));
    }
}
