package ls.EmployeeWorkOrderManagment.dto.user;

import lombok.Builder;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ls.EmployeeWorkOrderManagment.model.user.User}
 */
@Builder
public record UserCredentialsDto(String firstName,
                                 String lastName,
                                 String email,
                                 Set<String> roles,
                                 String password) implements Serializable {
}