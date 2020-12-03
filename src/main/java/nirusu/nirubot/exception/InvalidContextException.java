package nirusu.nirubot.exception;

public class InvalidContextException extends Exception {
    private static final long serialVersionUID = 1819861583136737249L;

    public InvalidContextException(String message) {
        super(message);
    }
}
