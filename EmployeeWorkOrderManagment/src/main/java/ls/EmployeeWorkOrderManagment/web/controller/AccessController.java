package ls.EmployeeWorkOrderManagment.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ls.EmployeeWorkOrderManagment.event.OnPasswordResetEvent;
import ls.EmployeeWorkOrderManagment.event.OnRegisterCompleteEvent;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.TokenService;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.TokenExpiredException;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
public class AccessController {

    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);
    private final UserService userService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AccessController(UserService userService, TokenService tokenService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "loginPage";
    }

    @GetMapping("/logout-success")
    public String getSuccessLogout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully!");
        return "redirect:/login";
    }

    @GetMapping("/invalid-session")
    public String getInvalidSessionPage() {
        return "invalid-session";
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
            logger.warn("User registration failed due to form validation errors.");
            return "register";
        } else {
            try{
                User savedUser = userService.saveNewUserAccount(user);
                applicationEventPublisher.publishEvent(new OnRegisterCompleteEvent(savedUser, request.getContextPath()));
                logger.info("User registered successfully: {}", savedUser.getEmail());
            } catch (UserAlreadyExistsException exists) {
                logger.error("User registration failed - User already exists {}", user.getEmail(), exists);
                model.addAttribute("message", exists.getMessage());
                return "register";
            } catch (NoSuchElementException element) {
                logger.error("User registration failed - no adequate roles exist.", element);
                model.addAttribute("message", element.getMessage());
                return "message";
            } catch (RuntimeException exception) {
                logger.error("User registration failed - activation email couldn't be sent: {}", user.getEmail(), exception);
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
            VerificationToken verifiedToken = tokenService.confirmUserRegistration(token);
            userService.enableUserAccount(verifiedToken);
            logger.info("User account enabled successfully with token: {} for user: {}", verifiedToken.getId(), verifiedToken.getUser().getEmail());
        } catch (InvalidTokenException | TokenExpiredException invalidTokenException) {
            logger.error("User account activation failed, provided token was invalid: {}", token);
            model.addAttribute("message", invalidTokenException.getMessage());
            return "message";
        }
        return "redirect:/login";
    }

    @GetMapping("/forgottenPassword")
    public String forgottenPassword(){
        return "forgottenpassword";
    }

    @PostMapping("/forgottenPassword")
    public String forgottenPasswordReset(@RequestParam(name = "email") String email,
                                         Model model,
                                         HttpServletRequest request) {
        try{
            User userAccount = userService.retrieveUserByEmail(email);
            applicationEventPublisher.publishEvent(new OnPasswordResetEvent(userAccount, email, request.getContextPath()));
            logger.info("Password reset link has been sent to user: {}", userAccount.getEmail());
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error("Resetting password failed due to account not found: {}", email);
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
            ResetToken fetchedToken = tokenService.confirmResetToken(token);
            request.getSession().setAttribute("token", fetchedToken);
            logger.info("Reset token id has been confirmed successfully: {}", fetchedToken.getId());
        } catch (InvalidTokenException | TokenExpiredException invalidTokenException) {
            logger.error("Token confirmation failed - provided token was invalid: {}", token);
            model.addAttribute("message", invalidTokenException.getMessage());
            return "message";
        }
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("message", "Password reset successful");
        logger.info("Password change form has been generated successfully");
        return "resetpassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute("user") UserPasswordChangeDto userRegistrationDto,
                                BindingResult bindingResult,
                                Model model,
                                HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            logger.warn("Password change failed due to errors in provided form: {}", bindingResult);
            return "resetpassword";
        } else {
            ResetToken token = (ResetToken) request.getSession().getAttribute("token");
            userService.resetUserPassword(userRegistrationDto, token);
            logger.info("Password has been changed successfully for user: {}", token.getUser().getEmail());
            model.addAttribute("message", "Password changed");
            return "message";
        }
    }
}
