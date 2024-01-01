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

@WebMvcTest(HomepageController.class)
public class HomepageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser
    void whenPromptedForHomepageThenReturnIndex() throws Exception {
        this.mockMvc.perform(get("")).andDo(print()).andExpect(status().isOk()).andExpect(view().name("index"));
    }
}
