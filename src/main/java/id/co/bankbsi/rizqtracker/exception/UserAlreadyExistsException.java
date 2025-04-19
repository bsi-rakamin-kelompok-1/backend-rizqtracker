package id.co.bankbsi.rizqtracker.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException (String message) {
        super(message);
    }
}
