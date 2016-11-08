package ch.joelniklaus.indoloc.activities;

import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import ch.joelniklaus.indoloc.R;

// extends Activity implements SensorEventListener
public class RangingTraining extends Activity implements SensorEventListener {
    //public class RangingTraining extends AppCompatActivity {
    static final int READ_BLOCK_SIZE = 100;
    public double magnetic;
    WifiReceiver wifiReceiver;
    WifiManager wifiManager;
    private String rss1, rss2, rss3, rss4, rss5, rss6, rss7;
    private TextView RSS1, RSS2, RSS3, RSS4, RSS5, RSS6, RSS7, scan;
    private Button btnStart;
    private EditText editPosition;
    private String rssData;
    private int scanNumber = 0;
    private SensorManager sensorManager;
    Sensor magnetometer;
    private boolean registering;

    Long tsLong;
    String ts;

    File file;

    //WIFI broadcaster class
    public class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            scan.setText(Integer.toString(scanNumber++));
            List<ScanResult> scanResults = wifiManager.getScanResults();

            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);

                // search by SSID
                switch (scanResult.SSID) {
                    case "My Passwort is Monkey":
                        rss1 = Integer.toString(scanResult.level);
                        RSS1.setText(rss1);
                    case "ADCH-Guest":
                        rss2 = Integer.toString(scanResult.level);
                        RSS2.setText(rss2);
                    case "ADCH-Intern":
                        rss3 = Integer.toString(scanResult.level);
                        RSS3.setText(rss3);
                    case "Core-Guest":
                        rss4 = Integer.toString(scanResult.level);
                        RSS4.setText(rss4);
                    case "Core-Intern":
                        rss5 = Integer.toString(scanResult.level);
                        RSS5.setText(rss5);
                    case "UPC248577407":
                        rss6 = Integer.toString(scanResult.level);
                        RSS6.setText(rss6);
                    case "UPC503960977":
                        rss7 = Integer.toString(scanResult.level);
                        RSS7.setText(rss7);
                }
            }
//          registerRSSI();
            registerRSSIExternal();
        }

        public void registerRSSIExternal() {
            tsLong = System.currentTimeMillis();
            ts = tsLong.toString();

            try {
                String position = editPosition.getText().toString();
                if (!(position == null)) {
                    String line = position + "\t" + rss1 + "\t" + rss2 + "\t" + rss3 + "\t" + rss4 + "\t" + rss5 + "\t" + rss6 + "\t" + ts + "\n\r";
                    if (registering) {
                        FileWriter w = new FileWriter(file, true);
                        w.append(line);
                        w.close();

                    }
                }
            } catch (IOException e) {
                alert("error: " + e.getMessage());
            }

        }

        public void registerRSSI() {

            try {
                String position = editPosition.getText().toString();
                if (!(position == null)) {
                    String line = position + "\t" + rss1 + "\t" + rss2 + "\t" + rss3 + "\t" + rss4 + "\t" + rss5 + "\t" + rss6;
                    if (registering) {
                        //Begin registering in txt file

                        FileOutputStream fileout = openFileOutput(rssData, MODE_APPEND);

                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                        outputWriter.append(line);
                        outputWriter.append("\n\r");
                        outputWriter.close();
                    }
                } else {
                    alert("You must define a position ...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging_training);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //WiFi
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();


        scan = (TextView) findViewById(R.id.txtScan);
        RSS1 = (TextView) findViewById(R.id.txtRSS1);
        RSS2 = (TextView) findViewById(R.id.txtRSS2);
        RSS3 = (TextView) findViewById(R.id.txtRSS3);
        RSS4 = (TextView) findViewById(R.id.txtRSS4);
        RSS5 = (TextView) findViewById(R.id.txtRSS5);
        RSS6 = (TextView) findViewById(R.id.txtRSS6);
        RSS7 = (TextView) findViewById(R.id.txtRSS7);
        btnStart = (Button) findViewById(R.id.btnStart);

        editPosition = (EditText) findViewById(R.id.editPosition);

        //txt file
        rssData = "rssData.txt";
        file = new File(getExternalFilesDir(null), rssData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

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
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //take the values
            magnetic = event.values[0];
        }

        wifiManager.startScan();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start(View view) {

        String label = btnStart.getText().toString();
        //alert(label);
        try {
            if (label.equals("START")) {
                scanNumber = 0;
                btnStart.setText("STOP");
                registering = true;
            } else {
                btnStart.setText("START");
                registering = false;
                //Stop registering in txt file

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create a path where we will place our private file on external storage.
    public void createFile(View view) {
        try {
            File file = new File(getExternalFilesDir(null), rssData);
            FileOutputStream os = new FileOutputStream(file);
            OutputStreamWriter out = new OutputStreamWriter(os);
            out.close();
            alert("File created!" + getExternalFilesDir(null).toString());
        } catch (IOException e) {
            alert("error");
        }
    }

    // Read text from file
    public void readFile(View v) {
        //reading text from file
        try {
            FileReader reader = new FileReader(file);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = reader.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            reader.close();
            alert("From file: " + s);

        } catch (IOException e) {
            alert("Error reading file: " + e.getMessage());
        }
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
