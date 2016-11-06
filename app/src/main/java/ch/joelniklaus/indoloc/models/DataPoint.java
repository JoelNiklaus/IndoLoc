package ch.joelniklaus.indoloc.models;

/**
 * Created by joelniklaus on 06.11.16.
 */
public class DataPoint {

    private String room;

    private double barometer;

    public DataPoint(String room, double barometer) {
        this.room = room;
        this.barometer = barometer;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public double getBarometer() {
        return barometer;
    }

    public void setBarometer(double barometer) {
        this.barometer = barometer;
    }
}
