package ls.EmployeeWorkOrderManagment.web.controller;

import jakarta.servlet.http.HttpSession;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping(path = "/dashboard/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserProfilePage(HttpSession session,
                                     Principal principal) {
        String userEmail = principal.getName();
        UserSiteRenderDto userData = userService.getUserInfoByEmail(userEmail);
        session.setAttribute("userData", userData);
        return "userProfile";
    }
}
