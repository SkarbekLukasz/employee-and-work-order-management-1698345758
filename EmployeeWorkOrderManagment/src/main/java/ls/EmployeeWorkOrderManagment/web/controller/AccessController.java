package ls.EmployeeWorkOrderManagment.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ls.EmployeeWorkOrderManagment.event.OnPasswordResetEvent;
import ls.EmployeeWorkOrderManagment.event.OnRegisterCompleteEvent;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.TokenExpiredException;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;

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

    @GetMapping("/forgottenPassword")
    public String forgottenPassword(Model model) {
        return "forgottenpassword";
    }

    @PostMapping("/forgottenPassword")
    public String forgottenPasswordReset(@RequestParam(name = "email") String email,
                                         Model model,
                                         ServletWebRequest request) {
        try{
            User userAccount = userService.retrieveUserByEmail(email);
            applicationEventPublisher.publishEvent(new OnPasswordResetEvent(userAccount, email, request.getContextPath()));
        } catch (UsernameNotFoundException usernameNotFoundException) {
            model.addAttribute("message", usernameNotFoundException.getMessage());
            return "message";
        }
        model.addAttribute("message", "Password reset token has been sent to provided email address");
        return "message";
    }

    @GetMapping("/resetPassword")
    public String resetPasswordForm(@RequestParam(name = "token") String token,
                                    Model model,
                                    HttpServletRequest request) {
        try{
            userService.confirmResetToken(token);
        } catch (InvalidTokenException | TokenExpiredException invalidTokenException) {
            model.addAttribute("message", invalidTokenException.getMessage());
            return "/message";
        }
        request.getSession().setAttribute("token", token);
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("message", "Password reset successful");
        return "resetPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute("user") UserPasswordChangeDto userRegistrationDto,
                                BindingResult bindingResult,
                                Model model,
                                HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            System.out.println("errory");
            return "resetPassword";
        } else {
            String token = (String) request.getSession().getAttribute("token");
            userService.changeUserPassword(userRegistrationDto, token);
            model.addAttribute("message", "Password changed");
            return "message";
        }
    }
}
