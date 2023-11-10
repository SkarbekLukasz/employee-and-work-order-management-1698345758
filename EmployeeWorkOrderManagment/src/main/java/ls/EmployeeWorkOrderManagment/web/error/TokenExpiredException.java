package ls.EmployeeWorkOrderManagment.web.error;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(String message) {
        super(message);
    }
}
