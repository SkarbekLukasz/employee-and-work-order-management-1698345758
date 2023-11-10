package ls.EmployeeWorkOrderManagment.persistence.model.token;

import java.util.Date;

public interface Token {
    Date calculateExpiryDate(int expiryTimeInMinutes);
}
