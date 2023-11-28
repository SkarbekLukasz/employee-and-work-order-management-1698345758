package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.persistence.dao.RoleRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.web.dto.role.RoleDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class RoleServiceIntegrationTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldGetAllRoles() {
        //given
        Role role1 = new Role();
        role1.setRoleName("ADMIN");
        Role role2 = new Role();
        role2.setRoleName("USER");
        roleRepository.save(role1);
        roleRepository.save(role2);

        //when
        Set<RoleDto> roles = roleService.getAllRoles();

        //then
        assertNotNull(roles);
        assertEquals(2, roles.size());
    }

}
