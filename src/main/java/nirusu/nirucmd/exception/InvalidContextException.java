package nirusu.nirucmd.exception;

public class InvalidContextException extends IllegalArgumentException {

    private static final long serialVersionUID = 2767440402747058816L;

    public InvalidContextException() {
        super();
    }

    public InvalidContextException(String message) {
        super(message);
    }
    
}
