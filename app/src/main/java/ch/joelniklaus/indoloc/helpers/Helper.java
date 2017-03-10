package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.widget.Toast;

import ch.joelniklaus.indoloc.BuildConfig;

/**
 * Created by joelniklaus on 04.03.2017
 */

public class Helper {

    public static void alert(Context context, String message) {
        alertShort(context, message);
    }

    public static void alertShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void alertLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void assertion(boolean condition) {
        if (BuildConfig.DEBUG && !condition) throw new AssertionError();
    }
}
