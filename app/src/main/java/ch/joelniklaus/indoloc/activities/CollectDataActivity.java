package ch.joelniklaus.indoloc.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.exceptions.InvalidRoomException;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.LocationHelper;
import ch.joelniklaus.indoloc.helpers.SensorHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.helpers.WifiHelper;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.LocationData;
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


// TODO Tag software when finished

// TODO Run Code inspection tools
// TODO add Log everywhere
// TODO cleanup Code
// TODO add Exception handling

/**
 * The activity which controls the data collection and also live testing of several trained classifiers based on the collected data.
 */
public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    public static final int LOCATION_PERMISSION = 100;
    public static final int STORAGE_PERMISSION = 200;

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
    private final LocationHelper locationHelper = new LocationHelper(this);


    /**
     * Is called when the user first starts the app. Sets up all the helpers,
     * asks for the location permission, sets up the text views and the prediction.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        sensorHelper.setUp();

        wifiHelper.setUp();

        locationHelper.setUp();
        // Request Permission for GPS (dangerous permission)
        showPermissionDialog();

        setUpTextViews();

        initPrediction();
    }

    /**
     * Is called when the user (re)opens the app. Registers the listeners and loads the saved data points.
     */
    @Override
    protected void onResume() {
        super.onResume();

        sensorHelper.registerListeners();

        wifiHelper.registerListeners();

        locationHelper.registerListeners();

        loadDataPoints();
    }


    /**
     * Is called when the user leaves the app. Unregisters the listeners and saves the collected data points.
     */
    @Override
    protected void onPause() {
        super.onPause();

        sensorHelper.unRegisterListeners();

        wifiHelper.unRegisterListeners();

        locationHelper.unRegisterListeners();

        saveDataPoints();
    }

    /**
     * Is called when a value of a sensor changes. Records all the data and saves it to the list of collected data points.
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        DataPoint previousDataPoint = null;
        if (currentDataPoint != null)
            previousDataPoint = SerializationUtils.clone(currentDataPoint);


        /*
        // IDEA: At start of each collection phase save magnetic base value
        if (dataPoints.isEmpty()) {
            magneticYBaseValue = magneticValues[0];
            magneticZBaseValue = magneticValues[1];
        }
        */

        //String landmark = landmarkEditText.getText().toString();

        String room = roomEditText.getText().toString();
        RSSData rssData = wifiHelper.readWifiData(getIntent());
        SensorData sensorData = sensorHelper.readSensorData(event);
        LocationData locationData = locationHelper.readLocationData();

        /*
        if (location != null)
            alert("Longitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude());
        else {
            alert("location is null");
        }
        */


        currentDataPoint = new DataPoint(room, sensorData, rssData, locationData);

        // Only collect datapoint which is different from the previous one
        if (!currentDataPoint.equals(previousDataPoint)) {
            saveDataPoint();
            predict();
        }

        setTextViewValues();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * Shows a dialog to the user which requests the location and the storage permission.
     */
    private void showPermissionDialog() {
        if (!checkLocationPermission(this)) {
            requestLocationPermission();
        }
        if (!checkStoragePermission(this)) {
            requestStoragePermission();
        }
    }

    /**
     * Requests from the user the permission to access the location data.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
    }

    /**
     * Requests from the user the permission to write to the external storage.
     */
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
    }

    /**
     * Checks if the location permission has been granted by the user.
     *
     * @param context
     * @return
     */
    public static boolean checkLocationPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks if the storage permission has been granted by the user.
     *
     * @param context
     * @return
     */
    public static boolean checkStoragePermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    alert("Location Permission granted!");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    alert("Location Permission denied!");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    alert("Storage Permission granted!");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    alert("Storage Permission denied!");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Sets up all the text views whose content has to be modified by the app during run time. Called once at the beginning.
     */
    private void setUpTextViews() {
        // TODO automate label initialization

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

        // TODO initialize labels with simple names of classifiers (getClass().getSimpleName())

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

    /**
     * Updates the content of the text views with the current values.
     */
    private void setTextViewValues() {
        SensorData sensorData = currentDataPoint.getSensorData();
        magneticYValue.setText(Float.toString(sensorData.getMagneticYProcessedOld()));
        magneticZValue.setText(Float.toString(sensorData.getMagneticZProcessedOld()));

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

    /**
     * Sets the classifiers for live (instant) prediction
     */
    private void initPrediction() {
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

    /**
     * Saves the until now collected data points to a temporary file. This is a security measure to aid data recovery if anything goes wrong.
     */
    private void saveDataPoints() {
        if (!dataPoints.isEmpty()) {
            fileHelper.saveDataPoints(dataPoints);
            createArffFile("temp.arff");
        }
    }

    /**
     * Loads the temporarily saved data points again. Belongs to data security system which prevents data loss.
     */
    private void loadDataPoints() {
        dataPoints = fileHelper.loadDataPoints();
    }

    /**
     * Starts the data collecting mode. If the app already is in data collecting mode stops it.
     *
     * @param view
     */
    public void startCollecting(View view) {
        String room = roomEditText.getText().toString();
        if (room.trim().equals("")) {
            alert("Please enter a room.");
            return;
        }
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

    /**
     * Saves the collected values as a data point to the list of data points.
     */
    private void saveDataPoint() {
        if (registering) {
            scanValue.setText(Integer.toString(scanNumber++));
            this.dataPoints.add(currentDataPoint);
        }
    }

    /**
     * Predicts the room resp. landmark based on the trained classifiers and the current data point.
     */
    public void predict() {
        if (predicting) {
            Instances data = null;
            try {
                data = WekaHelper.convertToSingleInstance(test, currentDataPoint);
            } catch (InvalidRoomException e) {
                e.printStackTrace();
                alert("Could not create single instance. Probably you entered an invalid room!");
                return;
            }
            try {
                for (int i = 0; i < NUMBER_OF_CLASSIFIERS; i++)
                    predictions.set(i, WekaHelper.predictInstance(classifiers.get(i), data));
            } catch (Exception e) {
                e.printStackTrace();
                alert(e.getMessage());
            }
        }
    }

    /**
     * Starts the live testing mode. If the application already is in the live testing mode stops it.
     *
     * @param view
     */
    public void startLiveTest(View view) {
        String room = roomEditText.getText().toString();
        if (room.trim().equals("")) {
            alert("Please enter a room.");
            return;
        }

        try {
            String label = liveTestButton.getText().toString();
            if (label.equals("START LIVE TEST")) {
                Instances train;
                try {
                    train = fileHelper.loadArffFromExternalStorage("train.arff");
                } catch (CouldNotLoadArffException e) {
                    alert("Please collect and save a train set first!");
                    return;
                }

                alert("Creating Test File ... ");
                try {
                    test = WekaHelper.convertToSingleInstance(train, currentDataPoint);
                } catch (InvalidRoomException e) {
                    e.printStackTrace();
                    alert("Could not create single instance. Probably you entered an invalid room!");
                    return;
                }

                // Build Classifiers
                alert("Training Models ...");
                for (Classifier classifier : classifiers) {
                    alert("Training " + classifier.getClass().getSimpleName() + " ...");
                    try {
                        classifier.buildClassifier(train);
                    } catch (Exception e) {
                        e.printStackTrace();
                        alert("Could not train classifier!");
                    }
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

    /**
     * Creates a train file (train.arff) from the collected data and saves it to the external storage.
     *
     * @param view
     */
    public void createTrainFile(View view) {
        String filePath = "train.arff";
        createArffFile(filePath);
    }

    /**
     * Creates a test file (test.arff) from the collected data and saves it to the external storage.
     *
     * @param view
     */
    public void createTestFile(View view) {
        String filePath = "test.arff";
        createArffFile(filePath);
    }

    /**
     * Creates an arff file from the list of collected data points and saves it to the external storage.
     *
     * @param filePath
     */
    private void createArffFile(String filePath) {
        if (dataPoints.isEmpty()) {
            alert("Please collect some Datapoints first!");
            return;
        }
        Instances data =  WekaHelper.buildInstances(dataPoints);

        try {
            //fileHelper.saveArffToInternalStorage(data, filePath);
            fileHelper.saveArffToExternalStorage(data, filePath);

            this.dataPoints = new ArrayList<>();
            this.scanNumber = 0;
            alert("Saved data points to " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
