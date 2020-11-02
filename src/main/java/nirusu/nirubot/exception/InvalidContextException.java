package nirusu.nirubot.exception;

public class InvalidContextException extends IllegalArgumentException {

    public InvalidContextException(final String message) {
        super(message);
    }

    public InvalidContextException(final String message, final Throwable from) {
        super(message, from);
    }
    
}
