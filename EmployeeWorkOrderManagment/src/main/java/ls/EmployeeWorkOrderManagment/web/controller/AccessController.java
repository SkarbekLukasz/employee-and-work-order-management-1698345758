package ls.EmployeeWorkOrderManagment.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ls.EmployeeWorkOrderManagment.event.OnRegisterCompleteEvent;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.TokenExpiredException;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;
@Controller
public class AccessController {
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AccessController(UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
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
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto user,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "register";
        } else {
            try{
                User savedUser = userService.saveNewUserAccount(user);
                applicationEventPublisher.publishEvent(new OnRegisterCompleteEvent(savedUser, request.getContextPath()));
            } catch (UserAlreadyExistsException exists) {
                model.addAttribute("message", exists.getMessage());
                return "register";
            } catch (NoSuchElementException element) {
                model.addAttribute("message", element.getMessage());
            } catch (RuntimeException exception) {
                model.addAttribute("message", "Error sending activation email.");
                return "message";
            }
            return "registrationCompletion";
        }

    }

    @GetMapping("/registerConfirm")
    public String confirmRegistration(@RequestParam(name = "token") String token,
                                      Model model) {
        try {
            userService.confirmUserRegistration(token);
        } catch (InvalidTokenException | TokenExpiredException invalidTokenException) {
            model.addAttribute("message", invalidTokenException.getMessage());
            return "/message";
        }
        return "/login";
    }
}
