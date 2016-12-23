package ch.joelniklaus.indoloc.models;

import java.io.Serializable;

/**
 * Created by joelniklaus on 06.11.16.
 */
public class DataPoint implements Serializable {

    private String room;

    private RSSData rssData;

    private SensorData sensorData;

    public DataPoint(String room, RSSData rssData, SensorData sensorData)  {
        this.room = room;
        this.rssData = rssData;
        this.sensorData = sensorData;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;

        DataPoint dataPoint = (DataPoint) o;

        if (!room.equals(dataPoint.room)) return false;
        if (!rssData.equals(dataPoint.rssData)) return false;
        return sensorData.equals(dataPoint.sensorData);

    }

    @Override
    public int hashCode() {
        int result = room.hashCode();
        result = 31 * result + rssData.hashCode();
        result = 31 * result + sensorData.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "room='" + room + '\'' +
                ", rssData=" + rssData +
                ", sensorData=" + sensorData +
                '}';
    }
}
