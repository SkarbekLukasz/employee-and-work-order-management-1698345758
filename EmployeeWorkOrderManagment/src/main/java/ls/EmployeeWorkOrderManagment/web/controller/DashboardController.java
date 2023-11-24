package ls.EmployeeWorkOrderManagment.web.controller;

import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getDashboardPage(Model model) {
        return "dashboard";
    }

    @GetMapping("/users")
    public String getUserList(Model model) {
        List<UserSiteRenderDto> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "userList";
    }

    @GetMapping("/users/delete")
    public String deleteUserAccount(@RequestParam String id) {
        userService.deleteUserAccount(id);
        return "redirect:/dashboard/users";
    }
}
