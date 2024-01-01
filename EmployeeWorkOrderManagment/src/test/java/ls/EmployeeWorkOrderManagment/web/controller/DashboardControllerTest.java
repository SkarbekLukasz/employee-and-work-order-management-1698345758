package ls.EmployeeWorkOrderManagment.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DashboardController.class)
public class DashboardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    void whenAuthorizedAndPromptedForDashboardThenReturnDashboardPage() throws Exception{
        this.mockMvc.perform(get("/dashboard")).andDo(print()).andExpect(status().isOk()).andExpect(view().name("dashboard"));
    }

    @Test
    void whenUnauthorizedAndPromptedForDashboardThenReturn401Status() throws Exception{
        this.mockMvc.perform(get("/dashboard")).andDo(print()).andExpect(status().isUnauthorized());
    }
}
