package ls.EmployeeWorkOrderManagment.web.controller;

import ls.EmployeeWorkOrderManagment.service.RoleService;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.role.RoleDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersDashboardController.class)
public class UsersDashboardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    RoleService roleService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDisplayAllUsersList() throws Exception{
        ArrayList<UserSiteRenderDto> users = new ArrayList<>();
        HashSet<RoleDto> roles = new HashSet<>();

        when(userService.getAllUsersInfo()).thenReturn(users);
        when(roleService.getAllRoles()).thenReturn(roles);

        this.mockMvc
                .perform(get("/dashboard/users"))
                .andExpect(view().name("userList"))
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("roles", roles));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserAccountOfAGivenId() throws Exception {
        String id = UUID.randomUUID().toString();

        this.mockMvc
                .perform(get("/dashboard/users/delete").param("id", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("delete", "User account successfully deleted!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDisplaySuccessfulEditMessage() throws Exception{
        String[] roles = new String[3];
        String enabled = "true";
        String id = UUID.randomUUID().toString();

        this.mockMvc
                .perform(post("/dashboard/users").with(csrf()).param("role", roles).param("radio-enabled", enabled).param("userId", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("edit", "Success! User account edited successfully!"));
    }
}
