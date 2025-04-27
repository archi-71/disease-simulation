package simulation.core;

/**
 * Exception class wrapper for simulation initialisation errors
 */
public class InitialisationException extends Exception {

    /**
     * Construct a new initialisation exception
     * @param message Error message to be shown to user
     */
    public InitialisationException(String message) {
        super(message);
    }
}
