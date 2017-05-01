package ch.joelniklaus.indoloc.models;

import java.io.Serializable;

/**
 * Data object containing all the measured location data of one data point.
 * <p>
 * Created by joelniklaus on 01.05.17.
 */

public class LocationData implements Serializable {

    private double latitude, longitude;

    public LocationData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationData)) return false;

        LocationData that = (LocationData) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        return Double.compare(that.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
