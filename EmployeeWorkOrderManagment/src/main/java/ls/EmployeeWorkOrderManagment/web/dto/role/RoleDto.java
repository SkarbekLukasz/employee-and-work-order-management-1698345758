package ls.EmployeeWorkOrderManagment.web.dto.role;

import lombok.Builder;

import java.util.UUID;
@Builder
public record RoleDto(UUID id,
                      String roleName) {
}
