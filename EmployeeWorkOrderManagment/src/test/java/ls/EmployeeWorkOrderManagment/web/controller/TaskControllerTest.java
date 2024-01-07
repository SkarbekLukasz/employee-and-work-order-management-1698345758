package ls.EmployeeWorkOrderManagment.web.controller;

import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.task.Task;
import ls.EmployeeWorkOrderManagment.persistence.model.task.TaskStatus;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.TaskService;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.task.TaskDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    TaskService taskService;

    @Test
    @WithMockUser
    void shouldDisplayValidTaskCreationForm() throws Exception {
        List<UserSiteRenderDto> designers = new ArrayList<>();

        when(userService.getAllUserByRole("DESIGNER")).thenReturn(designers);

        this.mockMvc
                .perform(get("/dashboard/task/task-creation"))
                .andExpect(model().attribute("designers", designers))
                .andExpect(view().name("taskCreation"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldSuccessfullyPostNewTask() throws Exception {

       TaskDto taskDto = new TaskDto(null, "project name", "description", "task code", null, null, null, UUID.randomUUID().toString());

        when(taskService.saveNewTask(any(TaskDto.class), any(String.class))).thenAnswer(invocation -> {
            TaskDto taskDtoArg = invocation.getArgument(0);
            String creatorUsernameArg = invocation.getArgument(1);

            UUID id = UUID.randomUUID();
            Set<Role> roles = new HashSet<>();
            roles.add(new Role(UUID.randomUUID(), "USER"));
            User commonUser = User.builder().id(id).email(creatorUsernameArg).lastName("last").firstName("first").roles(roles).password("$2a$12$j/hhBGn4q/n6Q9lyms8zmODeh9uxdh0aP60Mo2HPyclNW1JXjoCyq").enabled(true).picUrl("").build();

            id = UUID.randomUUID();
            roles = new HashSet<>();
            roles.add(new Role(UUID.randomUUID(), "DESIGNER"));
            User designerUser = User.builder().id(id).email("test2@test.com").lastName("last").firstName("first").roles(roles).password("$2a$12$j/hhBGn4q/n6Q9lyms8zmODeh9uxdh0aP60Mo2HPyclNW1JXjoCyq").enabled(true).picUrl("").build();

                    Task createdTask = Task.builder()
                    .id(UUID.randomUUID())
                    .taskCode(taskDtoArg.getTaskCode())
                    .assignedDesigner(designerUser)
                    .taskStatus(TaskStatus.OPEN)
                    .creator(commonUser)
                    .description(taskDtoArg.getDescription())
                    .taskNumber(1L)
                    .projectName(taskDtoArg.getProjectName())
                    .build();

            return createdTask;
        });

        this.mockMvc.perform(post("/dashboard/task")
                        .flashAttr("task", taskDto)
                        .with(csrf())
                        .principal(() -> "user")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/task/task-creation"));


        verify(taskService, times(1)).saveNewTask(taskDto, "user");
    }

    @Test
    @WithMockUser
    void shouldHandleUserNotFoundException() throws Exception {
        TaskDto taskDto = new TaskDto(null, "project name", "description", "task code", null, null, null, UUID.randomUUID().toString());

        when(taskService.saveNewTask(any(TaskDto.class), any(String.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/dashboard/task")
                        .flashAttr("task", taskDto)
                        .with(csrf())
                        .principal(() -> "username")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/task/task-creation"))
                .andExpect(flash().attribute("message", "Chosen designer cannot be selected for new task."));
    }

    @Test
    @WithMockUser
    void shouldHandleUsernameNotFoundException() throws Exception {
        TaskDto taskDto = new TaskDto(null, "project name", "description", "task code", null, null, null, UUID.randomUUID().toString());

        when(taskService.saveNewTask(any(TaskDto.class), any(String.class)))
                .thenThrow(new UsernameNotFoundException("Username not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/dashboard/task")
                        .flashAttr("task", taskDto)
                        .with(csrf())
                        .principal(() -> "username")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/task/task-creation"))
                .andExpect(flash().attribute("message", "Failed to save new task"));
    }

    @Test
    @WithMockUser
    void shouldHandleValidationErrors() throws Exception {
        TaskDto taskDto = new TaskDto(null, null, "description", "task code", null, null, null, UUID.randomUUID().toString());

        mockMvc.perform(post("/dashboard/task")
                        .flashAttr("task", taskDto)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/task/task-creation"))
                .andExpect(flash().attributeExists(BindingResult.MODEL_KEY_PREFIX + "task"))
                .andExpect(flash().attribute("task", taskDto));
    }
}
