package ls.EmployeeWorkOrderManagment.web.error;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
