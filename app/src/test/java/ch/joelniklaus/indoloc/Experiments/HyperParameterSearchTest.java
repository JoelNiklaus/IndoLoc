package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import java.util.Arrays;

import ch.joelniklaus.indoloc.AbstractTest;
import weka.classifiers.meta.AutoWEKAClassifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.meta.GridSearch;
import weka.classifiers.meta.MultiSearch;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HyperParameterSearchTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("exeter/train_small", "exeter/test_small");
    }

    @Test
    public void testCVParameterSelection() throws Exception {
        Instances train = loadFile("eigerstrasse/train_extended");
        J48 classifier = new J48();

        CVParameterSelection cvParameterSelection = new CVParameterSelection();
        cvParameterSelection.setClassifier(classifier);
        cvParameterSelection.buildClassifier(train);
        cvParameterSelection.setNumFolds(5);  // using 5-fold CV
        cvParameterSelection.addCVParameter("C 0.1 0.5 5");
        String[] classifierOptions = cvParameterSelection.getBestClassifierOptions();
        classifier.setOptions(classifierOptions);
        classifier.buildClassifier(train);
        System.out.println(Utils.joinOptions(classifierOptions));
        System.out.println(classifier.getClass().getSimpleName() +" with Parameters: "+ Arrays.toString(classifier.getOptions()));
    }

    @Test
    public void testGridSearch() throws Exception {
        Instances train = loadFile("eigerstrasse/train_extended");
        Bagging classifier = new Bagging();

        GridSearch gridSearch = new GridSearch();
        gridSearch.buildClassifier(train);

        System.out.println(classifier.getClass().getSimpleName() +" with Parameters: "+ Arrays.toString(classifier.getOptions()));

    }

    @Test
    public void testMultiSearch() throws Exception {
        Instances train = loadFile("eigerstrasse/train_extended");
        Bagging classifier = new Bagging();

        String[] options = {"-D", "-W", classifier.getClass().getName()};

        MultiSearch multiSearch = new MultiSearch();
        multiSearch.setOptions(options);
        multiSearch.setClassifier(classifier);

        multiSearch.buildClassifier(train);
        System.out.println(multiSearch.getBestClassifier().toString());

        System.out.println(classifier.getClass().getSimpleName() +" with Parameters: "+ Arrays.toString(classifier.getOptions()));

    }

    @Test
    public void testAutoWeka() throws Exception {
        Instances train = loadFile("eigerstrasse/train_extended");

        AutoWEKAClassifier autoweka = new AutoWEKAClassifier();
        autoweka.setTimeLimit(10); // in minutes
        autoweka.setMemLimit(1024); // in MB
        autoweka.setDebug(true);
        autoweka.setSeed(123);
        autoweka.setnBestConfigs(3);
        autoweka.buildClassifier(train);
        System.out.println(autoweka.getnBestConfigs());
    }
}