package ch.joelniklaus.indoloc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.LibSVMLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemovePercentage;

public class MainActivity extends AppCompatActivity {

    // Weka version = 3.7.3

    private String testTrain() {
        try {
            // Load
            Instances data = loadArffFromAssets("supermarket.arff");

            // Filtering
            RemovePercentage remove = new RemovePercentage();
            remove.setInputFormat(data);
            String trainingSetPercentage = "99";

            String[] optionsTrain = {"-P", trainingSetPercentage};
            remove.setOptions(optionsTrain);
            Instances newData = Filter.useFilter(data, remove);
            saveArff(newData, "train.arff");

            String[] optionsTest = {"-P", trainingSetPercentage, "-V"};
            remove.setOptions(optionsTest);
            newData = Filter.useFilter(data, remove);
            saveArff(newData, "test.arff");


            // Train and test set
            Instances train = loadArff("train.arff");   // from internal storage
            train.setClassIndex(data.numAttributes() - 1);
            Instances test = loadArff("test.arff");    // from internal storage
            test.setClassIndex(data.numAttributes() - 1);
            // train classifier
            Classifier cls = new J48();
            cls.buildClassifier(train);
            // evaluate classifier and print some statistics
            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(cls, test);

            String result = eval.toSummaryString("\nResults\n======\n", false);

            System.out.println(result);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    private String testClassifier() {
        try {
            Instances data = loadArffFromAssets("weather.arff");

            // Classifier
            String[] options = new String[1];
            options[0] = "-U";            // unpruned tree
            J48 tree = new J48();         // new instance of tree
            tree.setOptions(options);     // set the options

            // Evaluation
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(tree, data, 10, new Random(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    private String testLoadAndSave() {
        try {
            // Load
            Instances data = loadArffFromAssets("weather.arff");

            // Filtering
            String[] options = new String[2];
            options[0] = "-R";                                    // "range"
            options[1] = "1";                                     // first attribute
            Remove remove = new Remove();                         // new instance of filter
            remove.setOptions(options);                           // set options
            remove.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
            Instances newData = Filter.useFilter(data, remove);   // apply filter

            // Save
            saveArff(newData, "weather_new.arff");

            newData = loadArff("weather_new.arff");

            return newData.toSummaryString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "fail";
    }

    private String testSVM() {

        try {
            AbstractClassifier classifier = null;
            classifier = (AbstractClassifier) Class.forName(
                    "weka.classifiers.functions.LibSVM" ).newInstance();

            String options = ( "-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1" );
            String[] optionsArray = options.split( " " );

            classifier.setOptions( optionsArray );


            Instances train = loadArffFromAssets("weather.arff");

            LibSVMLoader svmLoader = new LibSVMLoader();
            svmLoader.getDataSet();

            classifier.buildClassifier( train );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "fail";
    }

    // save Arff to internal storage
    private void saveArff(Instances data, String filePath) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(getBaseContext().getFilesDir(), filePath)); // save to internal storage
        saver.writeBatch();
    }

    // Load Arff from internal storage
    private Instances loadArff(String filePath) throws Exception {
        DataSource source = new DataSource(getBaseContext().getFilesDir() + "/" + filePath);
        return source.getDataSet();
    }

    private Instances loadArffFromAssets(String filePath) throws Exception {
        DataSource source = new DataSource(getAssets().open(filePath));
        return source.getDataSet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonTap(View v) {
        Toast myToast = Toast.makeText(getApplicationContext(), testTrain(), Toast.LENGTH_LONG);
        myToast.show();
    }
}
