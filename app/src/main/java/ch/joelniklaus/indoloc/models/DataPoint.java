package ch.joelniklaus.indoloc.models;

import java.util.ArrayList;

/**
 * Created by joelniklaus on 06.11.16.
 */
public class DataPoint {

    private String room;

    private ArrayList<Integer> rssList;

    private SensorsValue sensors;

    public DataPoint(String room, ArrayList<Integer> rssList, SensorsValue sensors) {
        this.room = room;
        this.rssList = rssList;
        this.sensors = sensors;
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

    public SensorsValue getSensors() {
        return sensors;
    }

    public void setSensors(SensorsValue sensors) {
        this.sensors = sensors;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "room='" + room + '\'' +
                ", rssList=" + rssList +
                ", sensors=" + sensors +
                '}';
    }
}
