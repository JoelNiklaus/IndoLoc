package ch.joelniklaus.indoloc.exceptions;

/**
 * Thrown when it is not possible to load an arff file from the storage.
 *
 * Created by joelniklaus on 06.06.17.
 */

public class CouldNotLoadArffException extends Throwable {
    public CouldNotLoadArffException(String message) {
        super(message);
    }
}
