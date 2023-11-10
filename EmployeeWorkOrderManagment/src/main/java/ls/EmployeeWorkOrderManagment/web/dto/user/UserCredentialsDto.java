package ls.EmployeeWorkOrderManagment.web.dto.user;

import lombok.Builder;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link User}
 */
@Builder
public record UserCredentialsDto(String firstName,
                                 String lastName,
                                 String email,
                                 Set<String> roles,
                                 boolean enabled,
                                 String password) implements Serializable {
}