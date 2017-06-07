package ch.joelniklaus.indoloc.exceptions;

/**
 * Thrown when the headers of two datasets ar not the same
 *
 * Created by joelniklaus on 04.05.17.
 */

public class DifferentHeaderException extends Throwable {
    public DifferentHeaderException(String message) {
        super(message);
    }
}
