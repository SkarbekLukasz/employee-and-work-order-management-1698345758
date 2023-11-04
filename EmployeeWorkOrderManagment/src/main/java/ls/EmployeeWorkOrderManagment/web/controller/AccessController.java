package ls.EmployeeWorkOrderManagment.web.controller;

import jakarta.validation.Valid;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.NoSuchElementException;
@Controller
public class AccessController {

    private final UserService userService;

    public AccessController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "register";
        } else {
            try{
                userService.saveNewUserAccount(user);
            } catch (UserAlreadyExistsException exists) {
                model.addAttribute("message", exists.getMessage());
                return "register";
            } catch (NoSuchElementException element) {
                model.addAttribute("message", element.getMessage());
            }
            return "registrationConfirm";
        }

    }
}
