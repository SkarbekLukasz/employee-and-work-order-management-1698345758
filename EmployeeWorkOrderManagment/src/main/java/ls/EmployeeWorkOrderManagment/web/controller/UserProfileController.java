package ls.EmployeeWorkOrderManagment.web.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping(path = "/dashboard/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserProfilePage(HttpSession session,
                                     Principal principal,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        String userEmail = principal.getName();
        UserSiteRenderDto userData = userService.getUserInfoByEmail(userEmail);
        session.setAttribute("userData", userData);
        UserPasswordChangeDto passwordChange = new UserPasswordChangeDto();
        model.addAttribute("passwordChange", passwordChange);
        if(redirectAttributes.containsAttribute("message")) {
            model.addAttribute("message", redirectAttributes.getAttribute("message"));
        }
        return "userProfile";
    }

    @PostMapping("/firstName")
    public String changeFirstName(@RequestParam String userFirstName,
                                  Principal principal) {
        String userEmail = principal.getName();
        userService.changeUserFirstName(userFirstName, userEmail);
        return "redirect:/dashboard/profile";
    }

    @PostMapping("/lastName")
    public String changeLastName(@RequestParam String userLastName,
                                 Principal principal) {
        String userEmail = principal.getName();
        userService.changeUserLastName(userLastName, userEmail);
        return "redirect:/dashboard/profile";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("passwordChange") UserPasswordChangeDto userPasswordChange,
                                 BindingResult bindingResult,
                                 Principal principal) {
        if(bindingResult.hasErrors()) {
            return "userProfile";
        } else {
            String userEmail = principal.getName();
            userService.changeUserPassword(userPasswordChange, userEmail);
            return "redirect:/login";
        }
    }

    @PostMapping("/picture")
    public String changeProfilePicture(@RequestParam("picture_file") MultipartFile profilePicture,
                                       @RequestParam("userId") UUID userId,
                                       RedirectAttributes redirectAttributes) {
        try {
            userService.changeProfilePicture(profilePicture, userId);
        } catch (IOException ioException) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload a file");
            return "redirect:/dashboard/profile";
        } catch (UserNotFoundException userNotFoundException) {
            redirectAttributes.addFlashAttribute("message", userNotFoundException.getMessage());
            return "redirect:/dashboard/profile";
        }
        redirectAttributes.addFlashAttribute("message", "Picture uploaded successfully!");
        return "redirect:/dashboard/profile";
    }
}
