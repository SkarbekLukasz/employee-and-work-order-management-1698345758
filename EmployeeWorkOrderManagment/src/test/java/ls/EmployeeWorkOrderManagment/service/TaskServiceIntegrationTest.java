package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.EmployeeWorkOrderManagmentApplication;
import ls.EmployeeWorkOrderManagment.persistence.dao.TaskRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.task.Task;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.web.dto.task.TaskDto;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest(classes = {
        EmployeeWorkOrderManagmentApplication.class
})
@Transactional
@TestPropertySource(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15.2-alpine://db"
})
@Testcontainers
public class TaskServiceIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskService taskService;

    User commonUser;

    User designerUser;

    @BeforeEach
    void setUpObjects() {
        UUID id = UUID.randomUUID();
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(UUID.randomUUID(), "USER"));
        commonUser = User.builder().id(id).email("test@test.com").lastName("last").firstName("first").roles(roles).password("$2a$12$j/hhBGn4q/n6Q9lyms8zmODeh9uxdh0aP60Mo2HPyclNW1JXjoCyq").enabled(true).picUrl("").build();
        commonUser = userRepository.save(commonUser);

        id = UUID.randomUUID();
        roles = new HashSet<>();
        roles.add(new Role(UUID.randomUUID(), "DESIGNER"));
        designerUser = User.builder().id(id).email("test2@test.com").lastName("last").firstName("first").roles(roles).password("$2a$12$j/hhBGn4q/n6Q9lyms8zmODeh9uxdh0aP60Mo2HPyclNW1JXjoCyq").enabled(true).picUrl("").build();
        designerUser = userRepository.save(designerUser);
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldReturnValidSavedUser() {
        //given
        String creatorName = "test@test.com";
        TaskDto taskDto = TaskDto.builder()
                .description("balbaba")
                .projectName("project name")
                .taskCode("some code")
                .assignedDesignerId(designerUser.getId().toString())
                .build();

        //when
        Task savedTask = taskService.saveNewTask(taskDto, creatorName);

        //then
        Assertions.assertNotNull(savedTask);
        Assertions.assertEquals(savedTask.getTaskCode(), taskDto.getTaskCode());
        Assertions.assertEquals(savedTask.getCreator(), commonUser);
        Assertions.assertEquals(savedTask.getAssignedDesigner(), designerUser);
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldThrowUsernameNotFoundExceptionWhenGivenInvalidDesignerId() {
        //given
        String creatorName = "test@test.com";
        TaskDto taskDto = TaskDto.builder()
                .description("balbaba")
                .projectName("project name")
                .taskCode("some code")
                .assignedDesignerId(UUID.randomUUID().toString())
                .build();

        //when
        //then
        Assertions.assertThrows(UserNotFoundException.class, () -> taskService.saveNewTask(taskDto, creatorName));
    }
}
