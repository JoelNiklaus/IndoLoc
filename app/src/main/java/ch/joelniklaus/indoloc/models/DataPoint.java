package ch.joelniklaus.indoloc.models;

import java.util.ArrayList;

/**
 * Created by joelniklaus on 06.11.16.
 */
public class DataPoint {

    private String room;

    private ArrayList<Integer> rssList;

    private double barometer;

    public DataPoint(String room, ArrayList<Integer> rssList, double barometer) {
        this.room = room;
        this.rssList = rssList;
        this.barometer = barometer;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public ArrayList<Integer> getRssList() {
        return rssList;
    }

    public void setRssList(ArrayList<Integer> rssList) {
        this.rssList = rssList;
    }

    public double getBarometer() {
        return barometer;
    }

    public void setBarometer(double barometer) {
        this.barometer = barometer;
    }
}
