package ls.EmployeeWorkOrderManagment.web.dto.role;

import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;

public class RoleDtoMapper {

    public static RoleDto mapRoleToDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .build();
    }
}
