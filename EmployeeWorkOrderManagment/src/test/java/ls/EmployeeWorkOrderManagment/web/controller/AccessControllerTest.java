package ls.EmployeeWorkOrderManagment.web.controller;

import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.TokenService;
import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccessController.class)
public class AccessControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    TokenService tokenService;

    @MockBean
    ApplicationEventPublisher applicationEventPublisher;

    @Test
    @WithMockUser
    void testGetLoginPage() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldReturnSuccessfulLogoutPage() throws Exception {
        this.mockMvc
                .perform(get("/logout-success"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", "You have been logged out successfully!"));
    }

    @Test
    void shouldReturn401HttpStatusOnUnauthorizedLogoutRequest() throws Exception {
        this.mockMvc
                .perform(get("/logout-success"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401HttpStatusOnInvalidSessionRequestHeader() throws Exception {
        this.mockMvc
                .perform(get("/invalid-session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldReturnValidRegistrationFormOnRegisterRequest() throws Exception {
        this.mockMvc
                .perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", Matchers.any(UserRegistrationDto.class)));
    }

    @Test
    @WithMockUser
    void shouldPerformValidUserRegistrationProcessOnPostRequest() throws Exception {

        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .enabled(false)
                .password(UUID.randomUUID().toString())
                .roles(new HashSet<>()).build();

        when(userService.saveNewUserAccount(userRegistrationDto)).thenReturn(user);

        this.mockMvc
                .perform(post("/register").flashAttr("user", userRegistrationDto).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("registrationCompletion"));
    }

    @Test
    @WithMockUser
    void shouldReturnToRegisterPageWhenBindingResultHasErrors() throws Exception {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko1234!")
                .build();

        this.mockMvc
                .perform(post("/register").with(csrf()).flashAttr("user", userRegistrationDto))
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser
    void shouldReturnCorrectUserAlreadyExistsExceptionMessageWhenThrown() throws Exception {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();

        when(userService.saveNewUserAccount(userRegistrationDto)).thenThrow(new UserAlreadyExistsException("User already exists"));

        this.mockMvc
                .perform(post("/register").with(csrf()).flashAttr("user", userRegistrationDto))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "User already exists"));
    }

    @Test
    @WithMockUser
    void shouldReturnCorrectNoSuchElementExceptionMessageWhenThrown() throws Exception {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();

        when(userService.saveNewUserAccount(userRegistrationDto)).thenThrow(new NoSuchElementException("Failed to create account"));

        this.mockMvc
                .perform(post("/register").with(csrf()).flashAttr("user", userRegistrationDto))
                .andExpect(status().isOk())
                .andExpect(view().name("message"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Failed to create account"));
    }

    @Test
    @WithMockUser
    void shouldReturnCorrectRuntimeExceptionMessageWhenThrown() throws Exception {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();

        when(userService.saveNewUserAccount(userRegistrationDto)).thenThrow(new RuntimeException("Error sending activation email."));

        this.mockMvc
                .perform(post("/register").with(csrf()).flashAttr("user", userRegistrationDto))
                .andExpect(status().isOk())
                .andExpect(view().name("message"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Error sending activation email."));
    }

    @Test
    @WithMockUser
    void shouldRedirectToLoginPageWhenRegistrationIsConfirmed() throws Exception {
        //given
        String token = UUID.randomUUID().toString();

        //when
        when(tokenService.confirmUserRegistration(token)).thenReturn(new VerificationToken());

        this.mockMvc
                .perform(get("/registerConfirm").param("token", token))
                .andExpect(status().is3xxRedirection())
                .andDo(print())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void shouldDisplayInvalidTokenExceptionMessageWhenGivenInvalidVerificationToken() throws Exception {
        //given
        String token = UUID.randomUUID().toString();

        //when
        when(tokenService.confirmUserRegistration(token)).thenThrow(new InvalidTokenException("Invalid token provided"));

        this.mockMvc
                .perform(get("/registerConfirm").param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("message"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Invalid token provided"));
    }

    @Test
    @WithMockUser
    void shouldReturnForgottenPasswordForm() throws Exception {
        this.mockMvc
                .perform(get("/forgottenPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgottenpassword"));
    }

    @Test
    @WithMockUser
    void shouldDisplayActivationLinkSentMessageWhenGivenCorrectEmail() throws Exception {
        //given
        String email = "test@test.com";

        when(userService.retrieveUserByEmail(email)).thenReturn(new User());

        this.mockMvc
                .perform(post("/forgottenPassword").with(csrf()).param("email", email))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "Password reset token has been sent to provided email address"))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("message"));
    }

    @Test
    @WithMockUser
    void shouldDisplayUsernameNotFoundExceptionMessageWhenGivenInvalidEmail() throws Exception {
        //given
        String email = "test@test.com";

        when(userService.retrieveUserByEmail(email)).thenThrow(new UsernameNotFoundException("User with this email not found"));

        this.mockMvc
                .perform(post("/forgottenPassword").with(csrf()).param("email", email))
                .andExpect(status().isOk())
                .andExpect(view().name("message"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "User with this email not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldDisplayCorrectPasswordResetForm() throws Exception {
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        when(tokenService.confirmResetToken(token)).thenReturn(resetToken);

        this.mockMvc
                .perform(get("/resetPassword").param("token", token))
                .andExpect(MockMvcResultMatchers.request().sessionAttribute("token", resetToken))
                .andExpect(model().attribute("user", Matchers.any(UserRegistrationDto.class)))
                .andExpect(model().attribute("message", "Password reset successful"))
                .andExpect(view().name("resetpassword"));
    }

    @Test
    void shouldReturn401HttpStatusOnUnauthorizedResetPasswordRequest() throws Exception {
        this.mockMvc
                .perform(get("/resetPassword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldDisplayInvalidTokenExceptionMessageWhenGivenInvalidResetToken() throws Exception {
        //given
        String token = UUID.randomUUID().toString();

        //when
        when(tokenService.confirmResetToken(token)).thenThrow(new InvalidTokenException("Invalid token provided"));

        this.mockMvc
                .perform(get("/resetPassword").param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("message"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Invalid token provided"));
    }

    @Test
    @WithMockUser
    void shouldDisplaySuccessfulUserPasswordChangeWhenGivenCorrectPasswordChangeForm() throws Exception {
        //given
        UserPasswordChangeDto userPasswordChangeDto = UserPasswordChangeDto.builder()
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();
        ResetToken resetToken = new ResetToken();

        this.mockMvc
                .perform(post("/resetPassword").with(csrf()).sessionAttr("token", resetToken).flashAttr("user", userPasswordChangeDto))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "Password changed"))
                .andExpect(view().name("message"));
    }

    @Test
    @WithMockUser
    void shouldReturnToPasswordResetFormWhenBindingResultErrorsExist() throws Exception{
        UserPasswordChangeDto userPasswordChangeDto = UserPasswordChangeDto.builder()
                .password("Testowanko123!")
                .passwordConfirm("Testowanko1234!")
                .build();

        this.mockMvc
                .perform(post("/resetPassword").with(csrf()).flashAttr("user", userPasswordChangeDto))
                .andExpect(status().isOk())
                .andExpect(view().name("resetpassword"));
    }
}

