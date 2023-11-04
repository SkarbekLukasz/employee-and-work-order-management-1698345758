package ls.EmployeeWorkOrderManagment.persistence.dao;

import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleName(String roleName);
}
