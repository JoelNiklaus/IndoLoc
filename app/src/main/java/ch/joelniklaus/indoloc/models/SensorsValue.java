package ch.joelniklaus.indoloc.models;

/**
 * Created by joelniklaus on 11.11.16.
 */
public class SensorsValue {

    //private double ambientTemperature;
    //private double relativeHumidity;
    private double light;
    private double pressure;

    public SensorsValue(double light, double pressure) {
        //this.ambientTemperature = ambientTemperature;
        //this.relativeHumidity = relativeHumidity;
        this.light = light;
        this.pressure = pressure;

    }



    public double getLight() {
        return light;
    }

    public void setLight(double light) {
        this.light = light;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "SensorsValue{" +
                "light=" + light +
                ", pressure=" + pressure +
                '}';
    }
}
