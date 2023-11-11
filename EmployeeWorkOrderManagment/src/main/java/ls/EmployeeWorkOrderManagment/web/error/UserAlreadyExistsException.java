package ls.EmployeeWorkOrderManagment.web.error;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(final String message) {
    super(message);
    }
}
