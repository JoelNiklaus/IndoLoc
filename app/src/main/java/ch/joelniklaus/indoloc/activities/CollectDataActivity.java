package ch.joelniklaus.indoloc.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.SensorHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.helpers.WifiHelper;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.RSSData;
import ch.joelniklaus.indoloc.models.SensorData;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    //private TextView scanText, rss1Text, rss2Text, rss3Text, rss4Text, rss5Text, rss6Text, rss7Text, rss8Text, magneticYText, magneticZText;
    private TextView scanValue, rss1Value, rss2Value, rss3Value, rss4Value, rss5Value, rss6Value, rss7Value, rss8Value, magneticYValue, magneticZValue, predictNBValue, predictKNNValue, predictSVMValue, predictRFValue, predictBaggingValue, predictBoostingValue, predictMLPValue;
    private Button startButton, liveTestButton;
    private EditText roomEditText;//, landmarkEditText;

    private ArrayList<DataPoint> dataPoints = new ArrayList<>();
    private DataPoint currentDataPoint;
    private final ArrayList<String> predictions = new ArrayList<>(NUMBER_OF_CLASSIFIERS);
    private final ArrayList<Classifier> classifiers = new ArrayList<>(NUMBER_OF_CLASSIFIERS);

    private Instances test = null;

    private int scanNumber = 0;
    private boolean registering = false, predicting = false;
    private int magneticYBaseValue, magneticZBaseValue;

    private static final int NUMBER_OF_CLASSIFIERS = 7;

    private final FileHelper fileHelper = new FileHelper(this);
    private final SensorHelper sensorHelper = new SensorHelper(this);
    private final WifiHelper wifiHelper = new WifiHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        sensorHelper.setUp();

        wifiHelper.setUp();

        setUpTextViews();

        for (int i = 0; i < NUMBER_OF_CLASSIFIERS; i++)
            predictions.add("NO PREDICTION YET");

        classifiers.add(new NaiveBayes());
        classifiers.add(new IBk());
        classifiers.add(new LibSVM());
        classifiers.add(new RandomForest());
        classifiers.add(new Bagging());
        classifiers.add(new LogitBoost());
        classifiers.add(new MultilayerPerceptron());
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorHelper.registerListeners();

        wifiHelper.registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorHelper.unRegisterListeners();

        wifiHelper.unRegisterListeners();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        DataPoint previousDataPoint = null;
        if (currentDataPoint != null)
            previousDataPoint = SerializationUtils.clone(currentDataPoint);

        int[] magneticValues = sensorHelper.readSensorData(event);


        /*
        // At start of each collection phase save magnetic base value
        if (dataPoints.isEmpty()) {
            magneticYBaseValue = magneticValues[0];
            magneticZBaseValue = magneticValues[1];
        }
        */

        String room = roomEditText.getText().toString();
        //String landmark = landmarkEditText.getText().toString();
        RSSData rssData = wifiHelper.readWifiData(getIntent());
        SensorData sensorData = new SensorData(magneticValues[0], magneticValues[1]);
        currentDataPoint = new DataPoint(room, sensorData, rssData);

        // Only collect different DataPoints
        if (!currentDataPoint.equals(previousDataPoint)) {
            saveDataPoint();
            predict();
        }

        setTextViewValues();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void setUpTextViews() {
        //scanText = (TextView) findViewById(R.id.txtScanV);
        //scanText.setText("Scan Number");
        scanValue = (TextView) findViewById(R.id.txtScan);

        //rss1Text = (TextView) findViewById(R.id.txtRSS1V);
        //rss1Text.setText("My Passwort is Monkey");
        rss1Value = (TextView) findViewById(R.id.txtRSS1);

        //rss2Text = (TextView) findViewById(R.id.txtRSS2V);
        //rss2Text.setText("Chris Breezy");
        rss2Value = (TextView) findViewById(R.id.txtRSS2);

        //rss3Text = (TextView) findViewById(R.id.txtRSS3V);
        //rss3Text.setText("Core-Guest");
        rss3Value = (TextView) findViewById(R.id.txtRSS3);

        //rss4Text = (TextView) findViewById(R.id.txtRSS4V);
        //rss4Text.setText("ADCH-Guest");
        rss4Value = (TextView) findViewById(R.id.txtRSS4);

        //rss5Text = (TextView) findViewById(R.id.txtRSS5V);
        //rss5Text.setText("UPC503960977");
        rss5Value = (TextView) findViewById(R.id.txtRSS5);

        //rss6Text = (TextView) findViewById(R.id.txtRSS6V);
        //rss6Text.setText("UPC731B685");
        rss6Value = (TextView) findViewById(R.id.txtRSS6);

        //rss7Text = (TextView) findViewById(R.id.txtRSS7V);
        //rss7Text.setText("UPC2058401");
        rss7Value = (TextView) findViewById(R.id.txtRSS7);

        //rss8Text = (TextView) findViewById(R.id.txtRSS8V);
        //rss8Text.setText("UPC248577407");
        rss8Value = (TextView) findViewById(R.id.txtRSS8);

        //magneticYText = (TextView) findViewById(R.id.txtmagneticYV);
        //magneticYText.setText("MagneticY");
        magneticYValue = (TextView) findViewById(R.id.txtmagneticY);

        //magneticZText = (TextView) findViewById(R.id.txtmagneticZV);
        //magneticZText.setText("MagneticZ");
        magneticZValue = (TextView) findViewById(R.id.txtmagneticZ);

        predictNBValue = (TextView) findViewById(R.id.txtPredictNB);
        predictKNNValue = (TextView) findViewById(R.id.txtPredictKNN);
        predictSVMValue = (TextView) findViewById(R.id.txtPredictSVM);
        predictRFValue = (TextView) findViewById(R.id.txtPredictRF);
        predictBaggingValue = (TextView) findViewById(R.id.txtPredictBagging);
        predictBoostingValue = (TextView) findViewById(R.id.txtPredictBoosting);
        predictMLPValue = (TextView) findViewById(R.id.txtPredictMLP);

        startButton = (Button) findViewById(R.id.btnStartCollecting);
        liveTestButton = (Button) findViewById(R.id.btnStartLiveTest);

        roomEditText = (EditText) findViewById(R.id.editRoom);
        //landmarkEditText = (EditText) findViewById(R.id.editLandmark);
    }

    private void setTextViewValues() {
        SensorData sensorData = currentDataPoint.getSensorData();
        magneticYValue.setText(Integer.toString(sensorData.getMagneticY()));
        magneticZValue.setText(Integer.toString(sensorData.getMagneticZ()));

        RSSData rssData = currentDataPoint.getRssData();
        rss1Value.setText(rssData.getValues().get(0).toString());
        rss2Value.setText(rssData.getValues().get(1).toString());
        rss3Value.setText(rssData.getValues().get(2).toString());
        rss4Value.setText(rssData.getValues().get(3).toString());
        rss5Value.setText(rssData.getValues().get(4).toString());
        rss6Value.setText(rssData.getValues().get(5).toString());
        rss7Value.setText(rssData.getValues().get(6).toString());
        rss8Value.setText(rssData.getValues().get(7).toString());

        predictNBValue.setText(predictions.get(0));
        predictKNNValue.setText(predictions.get(1));
        predictSVMValue.setText(predictions.get(2));
        predictRFValue.setText(predictions.get(3));
        predictBaggingValue.setText(predictions.get(4));
        predictBoostingValue.setText(predictions.get(5));
        predictMLPValue.setText(predictions.get(6));
    }

    public void startCollecting(View view) {
        String label = startButton.getText().toString();
        try {
            // reset scan Number
            scanNumber = 0;
            if (label.equals("START COLLECTING")) {
                startButton.setText("STOP COLLECTING");

                // Start registering
                registering = true;
            } else {
                startButton.setText("START COLLECTING");
                // Stop registering
                registering = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDataPoint() {
        if (registering) {
            scanValue.setText(Integer.toString(scanNumber++));
            this.dataPoints.add(currentDataPoint);
        }
    }

    public void predict() {
        if (predicting) {
            try {
                Instances data = WekaHelper.convertToSingleInstance(test, currentDataPoint);

                for (int i = 0; i < NUMBER_OF_CLASSIFIERS; i++)
                    predictions.set(i, WekaHelper.predictInstance(classifiers.get(i), data));
            } catch (Exception e) {
                e.printStackTrace();
                alert(e.getMessage());
            }
        }
    }

    public void startLiveTest(View view) {
        String label = liveTestButton.getText().toString();
        try {
            if (label.equals("START LIVE TEST")) {

                Instances train = fileHelper.loadArffFromExternalStorage("train.arff");
                test = WekaHelper.convertToSingleInstance(train, currentDataPoint);

                // Build Classifiers
                alert("Training Models ...");
                for (Classifier classifier : classifiers) {
                    alert("Training "+classifier.getClass().getSimpleName()+" ...");
                    classifier.buildClassifier(train);
                    alert(classifier.getClass().getSimpleName() + " successfully trained!");
                }
                alert("Models successfully trained!");

                liveTestButton.setText("STOP LIVE TEST");

                // Start predicting
                predicting = true;
            } else {
                liveTestButton.setText("START LIVE TEST");

                // Stop predicting
                predicting = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    /*
    public void trainModels(View v) {
        try {
            Instances data = fileHelper.loadArffFromExternalStorage("train.arff");

            test = wekaHelper.trainForView(data);

            //fileHelper.saveArffToExternalStorage(test, "test.arff");
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void testModel(View v) {
        try {
            Instances test = fileHelper.loadArffFromExternalStorage("test.arff");

            wekaHelper.testForView(test);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void evaluateModel(View v) {
        try {
            Instances data = fileHelper.loadArffFromAssets("data.arff");

            wekaHelper.evaluateForView(data);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }


    public void readFile(View v) {
        String filePath = "data.arff";
        try {
            Instances instances = fileHelper.loadArffFromInternalStorage(filePath);
            Toast.makeText(this, instances.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, instances.toSummaryString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    public void createTrainFile(View view) {
        String filePath = "train.arff";
        createArffFile(filePath);
        this.dataPoints = new ArrayList<>();
        this.scanNumber = 0;
        alert("Saved data points to " + filePath);
    }

    public void createTestFile(View view) {
        String filePath = "test.arff";
        createArffFile(filePath);
        this.dataPoints = new ArrayList<>();
        this.scanNumber = 0;
        alert("Saved data points to " + filePath);
    }

    private void createArffFile(String filePath) {
        if (dataPoints.isEmpty())
            alert("Please collect some Datapoints first!");
        else
            try {
                Instances data = WekaHelper.buildInstances(dataPoints);

                fileHelper.saveArffToInternalStorage(data, filePath);
                fileHelper.saveArffToExternalStorage(data, filePath);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
