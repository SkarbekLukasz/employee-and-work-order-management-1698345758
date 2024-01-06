package ls.EmployeeWorkOrderManagment.web.controller;

import jakarta.validation.Valid;
import ls.EmployeeWorkOrderManagment.persistence.model.task.Task;
import ls.EmployeeWorkOrderManagment.service.TaskService;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.task.TaskDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/dashboard/task")
public class TaskController {

    private final UserService userService;
    private final TaskService taskService;

    public TaskController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("/task-creation")
    public String getTaskCreationForm(Model model,
                                      @ModelAttribute("message") String message) {
        List<UserSiteRenderDto> designers = userService.getAllUserByRole("DESIGNER");
        model.addAttribute("designers", designers);
        if(!model.containsAttribute("task")) {
            model.addAttribute("task", new TaskDto());
        }
        return "taskCreation";
    }

    @PostMapping("")
    public String postNewTask(@Valid @ModelAttribute("task") TaskDto newTask,
                              BindingResult bindingResult,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.task", bindingResult);
            redirectAttributes.addFlashAttribute("task", newTask);
        } else {
            try {
                Task createdTask = taskService.saveNewTask(newTask, principal.getName());
                String message = "Task has been created by: " + createdTask.getCreator().getFirstName() + " " + createdTask.getCreator().getLastName() + " for: " + createdTask.getAssignedDesigner().getFirstName() + " " + createdTask.getAssignedDesigner().getLastName();
                redirectAttributes.addFlashAttribute("message", message);
            } catch (UsernameNotFoundException usenameException) {
                redirectAttributes.addFlashAttribute("message", "Failed to save new task");
                return "redirect:/dashboard/task/task-creation";
            } catch (UserNotFoundException userException) {
                redirectAttributes.addFlashAttribute("message", "Chosen designer cannot be selected for new task.");
                return "redirect:/dashboard/task/task-creation";
            }
        }
        return "redirect:/dashboard/task/task-creation";
    }
}
