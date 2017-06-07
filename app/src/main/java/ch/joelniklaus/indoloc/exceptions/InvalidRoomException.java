package ch.joelniklaus.indoloc.exceptions;

/**
 * Thrown when the user entered an invalid room
 *
 * Created by joelniklaus on 06.06.17.
 */

public class InvalidRoomException extends Throwable {
    public InvalidRoomException(String message) {
        super(message);
    }
}
