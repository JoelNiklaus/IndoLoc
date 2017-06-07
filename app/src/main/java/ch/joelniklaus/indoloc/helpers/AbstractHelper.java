package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.widget.Toast;

import ch.joelniklaus.indoloc.BuildConfig;

/**
 * Created by joelniklaus on 04.03.2017
 */

public abstract class AbstractHelper {

    protected Context context;

    public AbstractHelper() {

    }

    public AbstractHelper(Context context) {
        this.context = context;
    }

    protected void alert(String message) {
        alertShort(message);
    }

    protected void alertShort(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    protected void alertLong(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    protected void assertion(boolean condition) {
        if (BuildConfig.DEBUG && !condition) throw new AssertionError();
    }

    protected String getTag() {
        return this.getClass().getName();
    }

    /**
     * Rounds the given number to a given number of decimal places.
     *
     * @param number
     * @param decimalPlaces
     * @return
     */
    public static float round(float number, int decimalPlaces) {
        float factor = (float) Math.pow(10, decimalPlaces);
        return Math.round(number * factor) / factor;
    }

    /**
     * Rounds the given number to a given nearest fraction.
     * Eg. round(0.523, 0.2) => 0.6
     *
     * @param number
     * @param fraction
     * @return
     */
    public static float round(float number, float fraction) {
        float factor = 1 / fraction;
        return Math.round(number * factor) / factor;
    }
}
