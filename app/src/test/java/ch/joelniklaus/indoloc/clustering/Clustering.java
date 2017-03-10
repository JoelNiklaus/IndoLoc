package ch.joelniklaus.indoloc.clustering;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import weka.clusterers.Canopy;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.Cobweb;
import weka.clusterers.EM;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class Clustering extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("exeter/train_margin", "exeter/test_margin");
    }

    @Test
    public void testDifferentClusterers() throws Exception {
        Instances trainClusterer = generateClusterData(train);

        buildAndEvaluateClusterer(trainClusterer, new Canopy());
        //buildAndEvaluateClusterer(trainClusterer, new Cobweb());
        buildAndEvaluateClusterer(trainClusterer, new EM());
        //buildAndEvaluateClusterer(trainClusterer, new FarthestFirst());
        //buildAndEvaluateClusterer(trainClusterer, new FilteredClusterer());
        //buildAndEvaluateClusterer(trainClusterer, new HierarchicalClusterer());
        //buildAndEvaluateClusterer(trainClusterer, new SimpleKMeans());
    }

    private void buildAndEvaluateClusterer(Instances trainClusterer, Clusterer clusterer) throws Exception {
        clusterer.buildClusterer(trainClusterer);
        evaluateClusterer(clusterer, test);
    }


    @Test
    public void testBatch() throws Exception {
        Instances trainClusterer = generateClusterData(train);

        String[] options = new String[2];
        options[0] = "-I";                  // max. iterations
        options[1] = "100";
        EM clusterer = new EM();            // new instance of clusterer
        clusterer.setOptions(options);      // set the options
        clusterer.buildClusterer(trainClusterer);    // build the clusterer

        // print results
        //System.out.println(eval.clusterResultsToString());
    }

    @Test
    public void testIncremental() throws Exception {
        Instances trainClusterer = generateClusterData(train);

        // train Cobweb
        Cobweb cw = new Cobweb();
        cw.buildClusterer(trainClusterer);
        for (Instance instance : trainClusterer)
            cw.updateClusterer(instance);
        cw.updateFinished();

        Clusterer clusterer = new EM();                                 // new clusterer instance, default options
        clusterer.buildClusterer(trainClusterer);                       // build clusterer

        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(clusterer);                                   // the cluster to evaluate
        eval.evaluateClusterer(test);                                   // data to evaluate the clusterer on

        // print results
        System.out.println(eval.clusterResultsToString());
        System.out.println("# of clusters: " + eval.getNumClusters());  // output # of clusters
    }


    /**
     * This method shows how to perform a "classes-to-clusters"
     * evaluation like in the Explorer using EM. The class needs as
     * first parameter an ARFF file to work on. The last attribute is
     * interpreted as the class attribute.
     * <p/>
     * This code is based on the method "startClusterer" of the
     * "weka.gui.explorer.ClustererPanel" class and the
     * "evaluateClusterer" method of the "weka.clusterers.ClusterEvaluation"
     * class.
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     */
    @Test
    public void testClassesToClusters() throws Exception {
        Instances trainClusterer = generateClusterData(train);

        // train clusterer
        EM clusterer = new EM();
        // set further options for EM, if necessary...
        clusterer.buildClusterer(trainClusterer);


        evaluateClusterer(clusterer, test);
    }

    private void evaluateClusterer(Clusterer clusterer, Instances test) throws Exception {
        // evaluate clusterer
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(clusterer);
        eval.evaluateClusterer(test);

        // print results
        System.out.println(eval.clusterResultsToString());
    }

    private Instances generateClusterData(Instances data) throws Exception {
        // generate data for clusterer (w/o class)
        Remove filter = new Remove();
        filter.setAttributeIndices("" + (data.classIndex() + 1));
        filter.setInputFormat(data);
        return Filter.useFilter(data, filter);
    }


}