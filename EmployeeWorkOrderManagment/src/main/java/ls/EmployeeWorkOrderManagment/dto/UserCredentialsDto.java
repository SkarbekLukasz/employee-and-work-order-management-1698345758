package ls.EmployeeWorkOrderManagment.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import ls.EmployeeWorkOrderManagment.model.role.Role;

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