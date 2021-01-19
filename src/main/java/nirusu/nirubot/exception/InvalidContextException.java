package nirusu.nirubot.exception;

import java.io.Serial;

public class InvalidContextException extends Exception {
    @Serial
    private static final long serialVersionUID = 1819861583136737249L;

    public InvalidContextException(String message) {
        super(message);
    }
}
