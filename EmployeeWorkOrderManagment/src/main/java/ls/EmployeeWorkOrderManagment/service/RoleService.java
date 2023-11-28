package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.persistence.dao.RoleRepository;
import ls.EmployeeWorkOrderManagment.web.dto.role.RoleDto;
import ls.EmployeeWorkOrderManagment.web.dto.role.RoleDtoMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Set<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDtoMapper::mapRoleToDto)
                .collect(Collectors.toSet());
    }
}
