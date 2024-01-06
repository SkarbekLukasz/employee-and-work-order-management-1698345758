package ls.EmployeeWorkOrderManagment.web.controller;

import ls.EmployeeWorkOrderManagment.service.RoleService;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.role.RoleDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/dashboard/users")
public class UsersDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(UsersDashboardController.class);

    private final UserService userService;
    private final RoleService roleService;

    public UsersDashboardController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public String getUserList(Model model,
                              @ModelAttribute("edit") String editAttribute,
                              @ModelAttribute("delete") String deleteAttribute) {
        List<UserSiteRenderDto> users = userService.getAllUsersInfo();
        model.addAttribute("users", users);
        Set<RoleDto> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        if(!editAttribute.isEmpty()) {
            model.addAttribute("edit", editAttribute);
        }
        if(!deleteAttribute.isEmpty()) {
            model.addAttribute("delete", deleteAttribute);
        }
        logger.info("Successfully loaded all user data.");
        return "userList";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete")
    public String deleteUserAccount(@RequestParam String id, RedirectAttributes redirectAttributes) {
        userService.deleteUserAccount(id);
        logger.warn("Successfully deleted account for user: {}", id);
        redirectAttributes.addFlashAttribute("delete", "User account successfully deleted!");
        return "redirect:/dashboard/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public String editUserAccount(@RequestParam(name = "role") Set<UUID> roles,
                                  @RequestParam(name = "radio-enabled") boolean enabled,
                                  @RequestParam(name = "userId") UUID userId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try{
            userService.editUserAccount(roles, enabled, userId);
        } catch (UserNotFoundException exception) {
            logger.error("User account not found for id: {}", userId.toString(), exception);
            model.addAttribute("message", exception.getMessage());
            return "message";
        }
        redirectAttributes.addFlashAttribute("edit", "Success! User account edited successfully!");
        logger.info("Successfully edited account for user: {}", userId.toString());
        return "redirect:/dashboard/users";
    }
}
