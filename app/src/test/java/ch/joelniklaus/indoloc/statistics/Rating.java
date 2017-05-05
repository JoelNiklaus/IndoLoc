package ch.joelniklaus.indoloc.statistics;

/**
 * Data Object for a Rating of a Classifier.
 *
 * Created by joelniklaus on 04.05.17.
 */

public abstract class Rating {

    protected String name;

    public Rating() {

    }

    public Rating(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }
}
