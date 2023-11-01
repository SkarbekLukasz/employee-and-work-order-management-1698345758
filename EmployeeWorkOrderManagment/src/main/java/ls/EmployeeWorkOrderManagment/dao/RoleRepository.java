package ls.EmployeeWorkOrderManagment.dao;

import ls.EmployeeWorkOrderManagment.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}
