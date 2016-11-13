package ch.joelniklaus.indoloc.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.SensorsValue;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Bagging;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    protected WekaHelper wekaHelper = new WekaHelper(this);
    protected ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();

    private TextView scanText, rss1Text, rss2Text, rss3Text, rss4Text, rss5Text, rss6Text, rss7Text, lightText, pressureText;
    private Button startButton;
    private EditText roomEditText;

    private WifiReceiver wifiReceiver;
    private WifiManager wifiManager;
    private ArrayList<Integer> rssList = new ArrayList<Integer>(7);
    ;

    private SensorManager sensorManager;
    private Sensor ambientTemperatureSensor, lightSensor, pressureSensor, relativeHumiditySensor;
    private double ambientTemperature, light, pressure, relativeHumidity;
    private SensorsValue sensorsValue = null;

    private Classifier classifier;

    private int scanNumber = 0;
    private boolean registering = false;

    private static final int NUMBER_OF_ACCESS_POINTS = 7;
    private static final File EXTERNAL_DIRECTORY = new File("sdcard", "Indoloc");

    //WIFI broadcaster class
    public class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            scanText.setText(Integer.toString(scanNumber++));
            List<ScanResult> scanResults = wifiManager.getScanResults();
            rssList = new ArrayList<Integer>(NUMBER_OF_ACCESS_POINTS);
            for (int i = 0; i < NUMBER_OF_ACCESS_POINTS; i++)
                rssList.add(i, 1);

            // search by SSID
            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);
                switch (scanResult.SSID) {
                    case "My Passwort is Monkey":
                        rssList.set(0, scanResult.level);
                        rss1Text.setText(Integer.toString(scanResult.level));
                    case "ADCH-Guest":
                        rssList.set(1, scanResult.level);
                        rss2Text.setText(Integer.toString(scanResult.level));
                    case "ADCH-Intern":
                        rssList.set(2, scanResult.level);
                        rss3Text.setText(Integer.toString(scanResult.level));
                    case "Core-Guest":
                        rssList.set(3, scanResult.level);
                        rss4Text.setText(Integer.toString(scanResult.level));
                    case "Core-Intern":
                        rssList.set(4, scanResult.level);
                        rss5Text.setText(Integer.toString(scanResult.level));
                    case "UPC248577407":
                        rssList.set(5, scanResult.level);
                        rss6Text.setText(Integer.toString(scanResult.level));
                    case "UPC503960977":
                        rssList.set(6, scanResult.level);
                        rss7Text.setText(Integer.toString(scanResult.level));
                }
            }

            saveDataPoint();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        // Sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

/*
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            alert("Success: ambient temperature");
        }
        else {
            alert("Failure: ambient temperature");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null){
            relativeHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            alert("Success: relative humidity");
        }
        else {
            alert("Failure: reltive humidity");
        }
        */

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            alert("Success: light");
        } else {
            alert("Failure: light");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            alert("Success: pressure");
        } else {
            alert("Failure: pressure");
        }

        // WiFi
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();


        scanText = (TextView) findViewById(R.id.txtScan);
        rss1Text = (TextView) findViewById(R.id.txtRSS1);
        rss2Text = (TextView) findViewById(R.id.txtRSS2);
        rss3Text = (TextView) findViewById(R.id.txtRSS3);
        rss4Text = (TextView) findViewById(R.id.txtRSS4);
        rss5Text = (TextView) findViewById(R.id.txtRSS5);
        rss6Text = (TextView) findViewById(R.id.txtRSS6);
        rss7Text = (TextView) findViewById(R.id.txtRSS7);
        lightText = (TextView) findViewById(R.id.txtLight);
        pressureText = (TextView) findViewById(R.id.txtPressure);
        startButton = (Button) findViewById(R.id.btnStart);
        roomEditText = (EditText) findViewById(R.id.editRoom);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //sensorManager.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_UI);
        //sensorManager.registerListener(this, relativeHumiditySensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_UI);

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        /*
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            //take the values
            ambientTemperature = event.values[0];
        }

         if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            //take the values
            relativeHumidity = event.values[0];
        }
        */

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            //take the values
            light = event.values[0];
            lightText.setText(Double.toString(light));
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            //take the values
            pressure = event.values[0];
            pressureText.setText(Double.toString(pressure));
        }

        sensorsValue = new SensorsValue(light, pressure);


        wifiManager.startScan();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start(View view) {

        String label = startButton.getText().toString();
        try {
            if (label.equals("START")) {
                scanNumber = 0;
                startButton.setText("STOP");
                // Start registering
                registering = true;
            } else {
                startButton.setText("START");
                // Stop registering
                registering = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDataPoint() {
        if (registering) {
            DataPoint dataPoint = registerDataPoint();
            this.dataPoints.add(dataPoint);
        }
    }

    @NonNull
    private DataPoint registerDataPoint() {
        String room = roomEditText.getText().toString();
        return new DataPoint(room, rssList, sensorsValue);
    }


    private void test(Instances test, Instances train) throws Exception {
        // evaluate classifier and print some statistics
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(classifier, test);
        alert(eval.toSummaryString("\nResults\n======\n", false));
    }

    public void liveTestModel(View v) {
        try {
            ArrayList<DataPoint> lastDataPoint = new ArrayList<DataPoint>(1);
            lastDataPoint.add(registerDataPoint());
            Instances test = buildInstances(lastDataPoint);
            Instances train = loadArffFromExternalStorage("train.arff");
            test(test, train);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void testModel(View v) {
        try {
            Instances test = loadArffFromExternalStorage("test.arff");
            Instances train = loadArffFromExternalStorage("train.arff");
            // evaluate classifier and print some statistics
            test(test, train);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void trainModel(View v) {
        try {
            Instances data = loadArffFromExternalStorage("data.arff");

            // Randomizing
            data.randomize(new Random());

            // Filtering
            RemovePercentage remove = new RemovePercentage();
            remove.setInputFormat(data);
            String trainingSetPercentage = "80";

            String[] optionsTrain = {"-P", trainingSetPercentage, "-V"};
            remove.setOptions(optionsTrain);
            Instances train = Filter.useFilter(data, remove);
            saveArffToExternalStorage(train, "train.arff");

            String[] optionsTest = {"-P", trainingSetPercentage};
            remove.setOptions(optionsTest);
            Instances test = Filter.useFilter(data, remove);
            saveArffToExternalStorage(test, "test.arff");

            train.setClassIndex(train.numAttributes() - 1);
            test.setClassIndex(test.numAttributes() - 1);

            trainKNN(train);

            alert(classifier.toString());
            alert("Model successfully trained.");
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    // K-Nearest Neighbour
    private void trainKNN(Instances train) throws Exception {
        // train classifier
        classifier = new IBk();
        classifier.buildClassifier(train);
    }

    /*
    // Support Vector Machine
    private void trainSVM(Instances train) throws Exception {
        // train classifier
        classifier = new SupportVectorMachineModel();
        classifier.buildClassifier(train);
    }
    */

    // Naive Bayes
    private void trainNB(Instances train) throws Exception {
        // train classifier
        classifier = new NaiveBayes();
        classifier.buildClassifier(train);
    }

    // Logistic Regression
    private void trainLR(Instances train) throws Exception {
        // train classifier
        classifier = new Logistic();
        classifier.buildClassifier(train);
    }

    // Bagging
    private void trainBagging(Instances train) throws Exception {
        // train classifier
        classifier = new Bagging();
        classifier.buildClassifier(train);
    }

    public void readFile(View v) {
        String filePath = "data.arff";
        try {
            Instances instances = loadArffFromInternalStorage(filePath);
            Toast.makeText(this, instances.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, instances.toSummaryString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFile(View v) {
        String filePath = "data.arff";
        createArffFile(filePath);
        this.dataPoints = new ArrayList<DataPoint>();
        alert("Saved data points to " + filePath);
    }

    private void createArffFile(String filePath) {
        Instances dataRaw = buildInstances(dataPoints);

        try {
            saveArffToInternalStorage(dataRaw, filePath);
            saveArffToExternalStorage(dataRaw, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Instances buildInstances(ArrayList<DataPoint> dataPoints) {
        //                                                     rooms + number of rss  + 2 sensors
        ArrayList<Attribute> attributes = new ArrayList<Attribute>(1 + rssList.size() + 2);
        ArrayList<String> rooms = new ArrayList<String>();

        for (DataPoint dataPoint : dataPoints)
            if (!rooms.contains(dataPoint.getRoom()))
                rooms.add(dataPoint.getRoom());
        /*
        rooms.add(getString(R.string.corridor));
        rooms.add(getString(R.string.kitchen));
        rooms.add(getString(R.string.living_room));
        rooms.add(getString(R.string.joel_room));
        rooms.add(getString(R.string.tobias_room));
        rooms.add(getString(R.string.nicola_room));
        */

        // room
        attributes.add(new Attribute("room", rooms));

        // rss
        for (int i = 0; i < rssList.size(); i++)
            attributes.add(new Attribute("rss" + i, Attribute.NUMERIC));

        // sensors
        //attributes.add(new Attribute("ambient_temperature", Attribute.NUMERIC));
        //attributes.add(new Attribute("relative_humidity", Attribute.NUMERIC));
        attributes.add(new Attribute("light", Attribute.NUMERIC));
        attributes.add(new Attribute("pressure", Attribute.NUMERIC));

        Instances data = new Instances("TestInstances", attributes, dataPoints.size());

        System.out.println("Before adding any instance");
        System.out.println("--------------------------");
        System.out.println(data);
        System.out.println("--------------------------");

        double[] instanceValues = null;
        for (DataPoint dataPoint : dataPoints) {
            instanceValues = new double[data.numAttributes()];

            // room
            instanceValues[0] = rooms.indexOf(dataPoint.getRoom());

            // rss
            ArrayList<Integer> rssListTemp = dataPoint.getRssList();
            ;
            for (int i = 0; i < rssListTemp.size(); i++)
                instanceValues[1 + i] = rssListTemp.get(i);

            // sensors
            SensorsValue sensors = dataPoint.getSensors();
            //instanceValues[rssListTemp.size() + 1] = sensors.getAmbientTemperature();
            //instanceValues[rssListTemp.size() + 4] = sensors.getRelativeHumidity();
            instanceValues[rssListTemp.size() + 1] = sensors.getLight();
            instanceValues[rssListTemp.size() + 2] = sensors.getPressure();

            data.add(new DenseInstance(1.0, instanceValues));

            System.out.println("After instance i");
            System.out.println("--------------------------");
            System.out.println(data);
            System.out.println("--------------------------");
        }

        System.out.println("After adding all instances");
        System.out.println("--------------------------");
        System.out.println(data);
        System.out.println("--------------------------");

        data.setClassIndex(data.numAttributes() - 1);

        return data;
    }

/*
    public void addDataPoint(View v) {
        dataPoints.add(new DataPoint(getRadioButtonText(), null, null));

        String output = "Data Point added:\n" +
                "Room: " + getRadioButtonText() + "\n" +
                "Barometer: ..." + "\n";

        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
    }

    private String getRadioButtonText() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
        return (String) radioButton.getText();
    }
*/

    public void saveArffToExternalStorage(Instances data, String fileName) throws IOException {
        if (isExternalStorageWritable()) {
            if (!EXTERNAL_DIRECTORY.exists())
                EXTERNAL_DIRECTORY.mkdirs();

            File file = new File(EXTERNAL_DIRECTORY, fileName);

            saveArff(data, file);
        } else
            Toast.makeText(this, "External Storage is not writable", Toast.LENGTH_SHORT).show();
    }

    public Instances loadArffFromExternalStorage(String fileName) throws Exception {
        if (isExternalStorageReadable()) {
            File file = new File(EXTERNAL_DIRECTORY, fileName);
            return loadArff(file.getAbsolutePath());
        }
        return null;
    }

    public void saveArffToInternalStorage(Instances data, String fileName) throws IOException {
        saveArff(data, new File(getFilesDir() + "/" + fileName));
    }


    public Instances loadArffFromInternalStorage(String fileName) throws Exception {
        return loadArff(getFilesDir() + "/" + fileName);
    }

    public Instances loadArffFromAssets(String fileName) throws Exception {
        Instances data =  new ConverterUtils.DataSource(getAssets().open(fileName)).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    private void saveArff(Instances data, File file) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(file);
        saver.writeBatch();
    }

    private Instances loadArff(String filePath) throws Exception {
        Instances data = new ConverterUtils.DataSource(filePath).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
