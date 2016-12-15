package ch.joelniklaus.indoloc.models;

/**
 * Created by joelniklaus on 06.11.16.
 */
public class DataPoint {

    private String room;

    private RSSData rssData;

    private SensorData sensorData;

    public DataPoint(String room, RSSData rssData, SensorData sensorData) {
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
    public String toString() {
        return "DataPoint{" +
                "room='" + room + '\'' +
                ", rssData=" + rssData +
                ", sensorData=" + sensorData +
                '}';
    }
}
