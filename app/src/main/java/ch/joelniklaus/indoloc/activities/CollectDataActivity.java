package ch.joelniklaus.indoloc.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.SensorHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.RSSData;
import ch.joelniklaus.indoloc.models.SensorData;
import weka.core.Instances;

public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    private TextView scanText, rss1Text, rss2Text, rss3Text, rss4Text, rss5Text, rss6Text, rss7Text, rss8Text, accelerometerText, magnetometerText;
    private TextView scanValue, rss1Value, rss2Value, rss3Value, rss4Value, rss5Value, rss6Value, rss7Value, rss8Value, accelerometerValue, magnetometerValue;
    private Button startButton;
    private EditText roomEditText;

    private WifiReceiver wifiReceiver;
    private WifiManager wifiManager;

    private ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
    private ArrayList<Integer> rssList = new ArrayList<Integer>(NUMBER_OF_ACCESS_POINTS);
    private SensorData sensorData;

    private int scanNumber = 0;
    private boolean registering = false;

    public static final int NUMBER_OF_ACCESS_POINTS = 8;
    public static final int NUMBER_OF_SENSORS = 2;


    private FileHelper fileHelper = new FileHelper(this);
    private SensorHelper sensorHelper = new SensorHelper(this);
    private WekaHelper wekaHelper = new WekaHelper(this);

    //WIFI broadcaster class
    public class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            rssList = new ArrayList<Integer>(NUMBER_OF_ACCESS_POINTS);
            for (int i = 0; i < NUMBER_OF_ACCESS_POINTS; i++)
                rssList.add(i, 1);

            // search by SSID
            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);
                int level = scanResult.level;

                switch (scanResult.BSSID) {
                    case "38:10:d5:0e:1f:25":
                        rssList.set(0, level);
                        rss1Value.setText(level + "");
                        rss1Text.setText("My Passwort is Monkey");
                    case "b4:ee:b4:60:fa:60":
                        rssList.set(1, level);
                        rss2Value.setText(level + "");
                        rss2Text.setText("Chris Breezy");
                    case "0e:18:d6:97:0c:8e":
                        rssList.set(2, level);
                        rss3Value.setText(level + "");
                        rss3Text.setText("Core-Guest");
                    case "82:2a:a8:17:34:b3":
                        rssList.set(3, level);
                        rss4Value.setText(level + "");
                        rss4Text.setText("ADCH-Guest");
                    case "14:49:e0:c9:ef:80":
                        rssList.set(4, level);
                        rss5Value.setText(level + "");
                        rss5Text.setText("UPC503960977");
                    case "56:67:51:ea.91:85":
                        rssList.set(5, level);
                        rss6Value.setText(level + "");
                        rss6Text.setText("UPC731B685");
                    case "c4:27:95:89:f3:5a":
                        rssList.set(6, level);
                        rss7Value.setText(level + "");
                        rss7Text.setText("UPC2058401");
                    case "14:49:e0:c9:ef:88":
                        rssList.set(7, level);
                        rss8Value.setText(level + "");
                        rss8Text.setText("UPC248577407");
                }


                //rss1Value.setText(rss1Value.getText() + "\nName: " + scanResult.SSID + "\nMAC:" + scanResult.BSSID);
            }
            saveDataPoint();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        // Sensors
        sensorHelper.setUpSensors();

        // WiFi
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        setUpTextViews();
    }

    private void setUpTextViews() {
        scanText = (TextView) findViewById(R.id.txtScanV);
        scanValue = (TextView) findViewById(R.id.txtScan);

        rss1Text = (TextView) findViewById(R.id.txtRSS1V);
        rss1Value = (TextView) findViewById(R.id.txtRSS1);
        rss2Text = (TextView) findViewById(R.id.txtRSS2V);
        rss2Value = (TextView) findViewById(R.id.txtRSS2);
        rss3Text = (TextView) findViewById(R.id.txtRSS3V);
        rss3Value = (TextView) findViewById(R.id.txtRSS3);
        rss4Text = (TextView) findViewById(R.id.txtRSS4V);
        rss4Value = (TextView) findViewById(R.id.txtRSS4);
        rss5Text = (TextView) findViewById(R.id.txtRSS5V);
        rss5Value = (TextView) findViewById(R.id.txtRSS5);
        rss6Text = (TextView) findViewById(R.id.txtRSS6V);
        rss6Value = (TextView) findViewById(R.id.txtRSS6);
        rss7Text = (TextView) findViewById(R.id.txtRSS7V);
        rss7Value = (TextView) findViewById(R.id.txtRSS7);
        rss8Text = (TextView) findViewById(R.id.txtRSS8V);
        rss8Value = (TextView) findViewById(R.id.txtRSS8);

        magnetometerText = (TextView) findViewById(R.id.txtMagnetometerV);
        magnetometerValue = (TextView) findViewById(R.id.txtMagnetometer);
        accelerometerText = (TextView) findViewById(R.id.txtAccelerometerV);
        accelerometerValue = (TextView) findViewById(R.id.txtAccelerometer);

        startButton = (Button) findViewById(R.id.btnStart);
        roomEditText = (EditText) findViewById(R.id.editRoom);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorHelper.registerListeners();

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorHelper.unRegisterListeners();

        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorData = sensorHelper.readSensorData(event);

        magnetometerValue.setText(Float.toString(sensorData.getMagneticY()));
        magnetometerText.setText("MagneticY");
        accelerometerValue.setText(Float.toString(sensorData.getMagneticZ()));
        accelerometerText.setText("MagneticZ");

        wifiManager.startScan();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start(View view) {
        String label = startButton.getText().toString();
        try {
            // reset scan Number
            scanNumber = 0;
            if (label.equals("START")) {
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
            scanValue.setText(Integer.toString(scanNumber++));
            scanText.setText("Scan Number");

            DataPoint dataPoint = registerDataPoint();
            this.dataPoints.add(dataPoint);
        }
    }

    @NonNull
    private DataPoint registerDataPoint() {
        String room = roomEditText.getText().toString();
        return new DataPoint(room, new RSSData(rssList), sensorData);
    }



    public void liveTestModel(View v) {
        try {
            Instances test = fileHelper.loadArffFromExternalStorage("test.arff");

            Instances data = WekaHelper.convertToSingleInstance(test, registerDataPoint());


            wekaHelper.testForView(data);
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

    public void trainModel(View v) {
        try {
            Instances data = fileHelper.loadArffFromExternalStorage("data.arff");

            Instances test = wekaHelper.trainForView(data);

            fileHelper.saveArffToExternalStorage(test, "test.arff");
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

    public void createFile(View v) {
        String filePath = "data.arff";
        createArffFile(filePath);
        this.dataPoints = new ArrayList<DataPoint>();
        alert("Saved data points to " + filePath);
    }

    private void createArffFile(String filePath) {
        Instances data = WekaHelper.buildInstances(dataPoints);

        try {
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
