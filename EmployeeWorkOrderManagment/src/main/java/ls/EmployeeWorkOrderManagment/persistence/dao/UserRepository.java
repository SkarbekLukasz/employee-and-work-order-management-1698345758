package ls.EmployeeWorkOrderManagment.persistence.dao;

import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u inner join u.roles roles where upper(roles.roleName) like upper(?1)")
    List<User> findByRoles_RoleNameLikeIgnoreCase(String roleName);

}
