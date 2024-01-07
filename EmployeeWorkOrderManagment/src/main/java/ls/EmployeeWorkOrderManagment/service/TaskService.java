package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.persistence.dao.TaskRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.task.Task;
import ls.EmployeeWorkOrderManagment.persistence.model.task.TaskStatus;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.web.dto.task.TaskDto;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserService userService,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public Task saveNewTask(TaskDto newTask, String creatorName) throws UsernameNotFoundException, UserNotFoundException {
        User taskCreator = userService.retrieveUserByEmail(creatorName);
        User assignedDesigner = userRepository.findById(UUID.fromString(newTask.getAssignedDesignerId())).orElseThrow(() -> new UserNotFoundException("Designer with this ID has not been found"));
        Task task = Task.builder()
                .taskCode(newTask.getTaskCode())
                .assignedDesigner(assignedDesigner)
                .taskStatus(TaskStatus.OPEN)
                .creator(taskCreator)
                .description(newTask.getDescription())
                .projectName(newTask.getProjectName())
                .build();
        return taskRepository.save(task);
    }
}
