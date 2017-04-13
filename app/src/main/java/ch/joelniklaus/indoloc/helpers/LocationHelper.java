package ch.joelniklaus.indoloc.helpers;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import ch.joelniklaus.indoloc.activities.CollectDataActivity;


/**
 * Reads and prepares gps or if not available network provider location values.
 * <p>
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
 * <p>
 * Created by joelniklaus on 27.03.17.
 */
public class LocationHelper extends AbstractHelper {

    private LocationManager locationManager;

    protected Location location;

    double latitude;
    double longitude;

    public LocationHelper(Context context) {
        super(context);
    }

    /**
     * Sets up the sensor manager and assigns the fields. Needs to be called before any reading of the sensors can happen.
     */
    public void setUp() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Registers the listener of the gps. Must be called before reading location data.
     */
    public void registerListeners() {
        LocationListener locationListener = new LocationListener() {
            private Location location;

            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                this.location = location;
                alert("Provider: " + location.getProvider() + ", Accuracy: " + location.getAccuracy() + ", Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(getTag(), "onStatusChanged: " + provider);
            }

            public void onProviderEnabled(String provider) {
                Log.e(getTag(), "onProviderEnabled: " + provider);
            }

            public void onProviderDisabled(String provider) {
                Log.e(getTag(), "onProviderDisabled: " + provider);
            }
        };

        if (!CollectDataActivity.checkPermission(context)) {
            alert("Please grant permission to access gps data");
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showSettingsAlert();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /**
     * Unregisters the listener of the gps. Should be called after reading location data.
     */
    public void unRegisterListeners() {
        locationManager.removeUpdates((LocationListener) context);
    }

    public Location readLocationData() {
        if (!CollectDataActivity.checkPermission(context)) {
            alert("Please grant permission to access gps data");
            return null;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showSettingsAlert();
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}

