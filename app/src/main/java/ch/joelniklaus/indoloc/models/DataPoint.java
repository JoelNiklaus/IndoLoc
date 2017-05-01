package ch.joelniklaus.indoloc.models;

import java.io.Serializable;

/**
 * Data object containing all the information collected in one specific point.
 * <p>
 * Created by joelniklaus on 06.11.16.
 */

public class DataPoint implements Serializable {

    private String room;
    private RSSData rssData;
    private SensorData sensorData;
    private LocationData locationData;

    public DataPoint(String room, SensorData sensorData, RSSData rssData, LocationData locationData) {
        this.room = room;
        this.sensorData = sensorData;
        this.rssData = rssData;
        this.locationData = locationData;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public RSSData getRssData() {
        return rssData;
    }

    public void setRssData(RSSData rssData) {
        this.rssData = rssData;
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocation(LocationData locationData) {
        this.locationData = locationData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;

        DataPoint dataPoint = (DataPoint) o;

        if (room != null ? !room.equals(dataPoint.room) : dataPoint.room != null) return false;
        if (rssData != null ? !rssData.equals(dataPoint.rssData) : dataPoint.rssData != null)
            return false;
        if (sensorData != null ? !sensorData.equals(dataPoint.sensorData) : dataPoint.sensorData != null)
            return false;
        return locationData != null ? locationData.equals(dataPoint.locationData) : dataPoint.locationData == null;

    }

    @Override
    public int hashCode() {
        int result = room != null ? room.hashCode() : 0;
        result = 31 * result + (rssData != null ? rssData.hashCode() : 0);
        result = 31 * result + (sensorData != null ? sensorData.hashCode() : 0);
        result = 31 * result + (locationData != null ? locationData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "room='" + room + '\'' +
                ", rssData=" + rssData +
                ", sensorData=" + sensorData +
                ", locationData=" + locationData +
                '}';
    }
}
