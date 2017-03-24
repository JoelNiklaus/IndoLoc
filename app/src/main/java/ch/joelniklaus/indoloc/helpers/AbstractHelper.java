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
}
