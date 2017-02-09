package ch.joelniklaus.indoloc.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import ch.joelniklaus.indoloc.models.RSSData;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by joelniklaus on 19.12.16.
 */
public class WifiHelper {

    private final Context context;

    private WifiReceiver wifiReceiver;
    private WifiManager wifiManager;

    private ArrayList<Integer> rssList = new ArrayList<>(NUMBER_OF_ACCESS_POINTS);
    public static final int NUMBER_OF_ACCESS_POINTS = 8;

    public WifiHelper(Context context) {
        this.context = context;
    }

    public void setUp() {
        wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
    }

    public void registerListeners() {
        context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void unRegisterListeners() {
        context.unregisterReceiver(wifiReceiver);
    }

    public RSSData readWifiData(Intent intent) {
        wifiManager.startScan();
        wifiReceiver.onReceive(context, intent);
        return new RSSData(rssList);
    }

    //WIFI broadcaster class
    public class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            rssList = new ArrayList<>(NUMBER_OF_ACCESS_POINTS);
            for (int i = 0; i < NUMBER_OF_ACCESS_POINTS; i++)
                rssList.add(i, 1);

            // search by MAC address
            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);
                int level = scanResult.level;

/* Eigerstrasse
                switch (scanResult.BSSID) {
                    case "38:10:d5:0e:1f:25":
                        rssList.set(0, level);
                    case "b4:ee:b4:60:fa:60":
                        rssList.set(1, level);
                    case "0e:18:d6:97:0c:8e":
                        rssList.set(2, level);
                    case "82:2a:a8:17:34:b3":
                        rssList.set(3, level);
                    case "14:49:e0:c9:ef:80":
                        rssList.set(4, level);
                    case "56:67:51:ea.91:85":
                        rssList.set(5, level);
                    case "c4:27:95:89:f3:5a":
                        rssList.set(6, level);
                    case "14:49:e0:c9:ef:88":
                        rssList.set(7, level);
                }
 */

 /* CDS
                switch (scanResult.SSID) {
                    case "ap1":
                        rssList.set(0, level);
                    case "ap2":
                        rssList.set(1, level);
                    case "ap3":
                        rssList.set(2, level);
                    case "ap4":
                        rssList.set(3, level);
                    case "ap5":
                        rssList.set(4, level);
                    case "APL1":
                        rssList.set(5, level);
                    case "public-unibe":
                        rssList.set(6, level);
                    case "eduroam":
                        rssList.set(7, level);
                }
*/

 /* Rickenbach
                switch (scanResult.SSID) {
                    case "jxx-10375":
                        rssList.set(0, level);
                    case "Phone not found":
                        rssList.set(1, level);
                    case "2":
                        rssList.set(2, level);
                    case "3":
                        rssList.set(3, level);
                    case "4":
                        rssList.set(4, level);
                    case "5":
                        rssList.set(5, level);
                    case "6":
                        rssList.set(6, level);
                    case "7":
                        rssList.set(7, level);
                }
*/

                 /* Exeter James Owen Court
                switch (scanResult.SSID) {
                    case "Studentcom":
                        rssList.set(0, level);
                    case "eduroam":
                        rssList.set(1, level);
                    case "Phone not found":
                        rssList.set(2, level);
                    case "Exeter":
                        rssList.set(3, level);
                    case "BTWifi-X":
                        rssList.set(4, level);
                    case "VM5449112":
                        rssList.set(5, level);
                    case "stagecoach-wifi":
                        rssList.set(6, level);
                    case "7":
                        rssList.set(7, level);
                }
*/
                /* Exeter University Campus */
                switch (scanResult.SSID) {
                    case "eduroam":
                        rssList.set(0, level);
                    case "UoE_Open":
                        rssList.set(1, level);
                    case "UoE_Guest":
                        rssList.set(2, level);
                    case "3":
                        rssList.set(3, level);
                    case "4":
                        rssList.set(4, level);
                    case "5":
                        rssList.set(5, level);
                    case "6":
                        rssList.set(6, level);
                    case "7":
                        rssList.set(7, level);
                }

            }
        }
    }
}
