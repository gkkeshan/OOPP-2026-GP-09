package ftms.exception;

/** Thrown when a mark is outside 0-100. Unchecked, so setters stay simple. */
public class InvalidMarkException extends RuntimeException {
    private String message;

    public InvalidMarkException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() { return message; }
}
