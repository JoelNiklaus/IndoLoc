package ch.joelniklaus.indoloc.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.joelniklaus.indoloc.models.RSSData;

import static android.content.Context.WIFI_SERVICE;

/**
 * Reads and prepares rss values from specified nearby access points.
 * <p>
 * Created by joelniklaus on 19.12.16.
 */
public class WifiHelper extends AbstractHelper {

    private final Context context;

    private WifiReceiver wifiReceiver;
    private WifiManager wifiManager;

    private ArrayList<Integer> rssList = new ArrayList<>(NUMBER_OF_ACCESS_POINTS);
    public static final int NUMBER_OF_ACCESS_POINTS = 10;

    public WifiHelper(Context context) {
        this.context = context;
    }

    /**
     * Sets up the wifi manager and the wifi receiver. Has to be called before reading data.
     */
    public void setUp() {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        for (int i = 0; i < NUMBER_OF_ACCESS_POINTS; i++)
            rssList.add(i, 0);
    }

    /**
     * Registers the receiver. Has to be called before reading data.
     */
    public void registerListeners() {
        context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    /**
     * Unregisters the receiver. Should be called after reading data.
     */
    public void unRegisterListeners() {
        context.unregisterReceiver(wifiReceiver);
    }

    /**
     * Reads the wifi signals and returns a list with one rss value per access point which has been registered.
     *
     * @param intent
     * @return
     */
    public RSSData readWifiData(Intent intent) {
        wifiManager.startScan();
        wifiReceiver.onReceive(context, intent);
        //System.out.println(Arrays.toString(rssList.toArray()));
        if (rssList == null) {
            Log.w(getTag(), "Rss list is null");
        }
        return new RSSData(rssList);
    }

    /**
     * Inner class used to scan the network. Receives the rss signals.
     * In the switch statement the access points which matter can be specified.
     */
    public class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> scanResults = wifiManager.getScanResults();

            // search by MAC address or Network Name
            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);
                int level = scanResult.level;

/* Eigerstrasse
                switch (scanResult.BSSID) {
                    case "38:10:d5:0e:1f:25":
                        rssList.set(0, level);
                        break;
                    case "b4:ee:b4:60:fa:60":
                        rssList.set(1, level);
                        break;
                    case "0e:18:d6:97:0c:8e":
                        rssList.set(2, level);
                        break;
                    case "82:2a:a8:17:34:b3":
                        rssList.set(3, level);
                        break;
                    case "14:49:e0:c9:ef:80":
                        rssList.set(4, level);
                        break;
                    case "56:67:51:ea.91:85":
                        rssList.set(5, level);
                        break;
                    case "c4:27:95:89:f3:5a":
                        rssList.set(6, level);
                        break;
                    case "14:49:e0:c9:ef:88":
                        rssList.set(7, level);
                        break;
                }
 */

 /* CDS
                switch (scanResult.SSID) {
                    case "ap1":
                        rssList.set(0, level);
                        break;
                    case "ap2":
                        rssList.set(1, level);
                        break;
                    case "ap3":
                        rssList.set(2, level);
                        break;
                    case "ap4":
                        rssList.set(3, level);
                        break;
                    case "ap5":
                        rssList.set(4, level);
                        break;
                    case "APL1":
                        rssList.set(5, level);
                        break;
                    case "APL3":
                        rssList.set(6, level);
                        break;
                    case "Swisscom_Auto_Login":
                        rssList.set(7, level);
                        break;
                    case "eduroam":
                        rssList.set(8, level);
                        break;
                    case "public-unibe":
                        rssList.set(9, level);
                        break;
                }
*/

 /* Rickenbach*/
                switch (scanResult.SSID) {
                    case "jxx-10375":
                        rssList.set(0, level);
                        break;
                    case "Phone not found":
                        rssList.set(1, level);
                        break;
                    case "2":
                        rssList.set(2, level);
                        break;
                    case "3":
                        rssList.set(3, level);
                        break;
                    case "4":
                        rssList.set(4, level);
                        break;
                    case "5":
                        rssList.set(5, level);
                        break;
                    case "6":
                        rssList.set(6, level);
                        break;
                    case "7":
                        rssList.set(7, level);
                        break;
                    case "8":
                        rssList.set(8, level);
                        break;
                    case "9":
                        rssList.set(9, level);
                        break;
                }

                 /* Exeter James Owen Court
                switch (scanResult.BSSID) {
                    case "00:c0:49:d8:db:e6": // University of Exeter
                        rssList.set(0, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                    case "00:18:e7:c7:cb:88": // Graphene Centre
                        rssList.set(1, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                    case "04:da:d2:9d:0a:c0": // Studentcom
                        rssList.set(2, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                    case "04:da:d2:9d:0a:cf": // Studentcom
                        rssList.set(3, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                    case "b4:e9:b0:a6:40:50": // Studentcom
                        rssList.set(4, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                    case "04:da:d2:9c:a9:d0": // Studentcom
                        rssList.set(5, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                    case "04:da:d2:9c:a9:d2": // eduroam
                        rssList.set(6, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                    case "b4:e9:b0:a6:40:52": // eduroam
                        rssList.set(7, level);
                        //System.out.println(i + " Name: "+scanResult.SSID +", MAC: "+ scanResult.BSSID +", Level: "+ level);
                        break;
                }
*/

                /* Exeter University Campus

                switch (scanResult.SSID) {
                    case "eduroam":
                        rssList.set(0, level);
                        break;
                    case "UoE_Open":
                        rssList.set(1, level);
                        break;
                    case "UoE_Guest":
                        rssList.set(2, level);
                        break;
                    case "3":
                        rssList.set(3, level);
                        break;
                    case "4":
                        rssList.set(4, level);
                        break;
                    case "5":
                        rssList.set(5, level);
                        break;
                    case "6":
                        rssList.set(6, level);
                        break;
                    case "7":
                        rssList.set(7, level);
                        break;
                }
                */
            }
        }
    }
}

