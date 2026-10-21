package ftms.exception;


public class TimetableConflictException extends Exception {

    public TimetableConflictException(String message) {
        super(message);
    }

    public TimetableConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}