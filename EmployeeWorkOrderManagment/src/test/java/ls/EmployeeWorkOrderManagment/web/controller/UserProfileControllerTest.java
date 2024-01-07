package ls.EmployeeWorkOrderManagment.web.controller;

import ls.EmployeeWorkOrderManagment.service.UserService;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @WithMockUser
    void shouldRedirectToProfileAfterReceivingPostRequestWithFirstNameChange() throws Exception{

        this.mockMvc
                .perform(post("/dashboard/profile/firstName").with(csrf()).param("userFirstName", "last"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void shouldRedirectToProfileAfterReceivingPostRequestWithLastNameChange() throws Exception{

        this.mockMvc
                .perform(post("/dashboard/profile/lastName").with(csrf()).param("userLastName", "first"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void shouldRedirectToProfileAfterReceivingPostRequestWithPasswordChange() throws Exception{

        UserPasswordChangeDto userPasswordChangeDto = UserPasswordChangeDto.builder()
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();

        this.mockMvc
                .perform(post("/dashboard/profile/password").with(csrf()).flashAttr("passwordChange", userPasswordChangeDto))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void shouldReturnToProfileAfterReceivingInvalidPostRequestWithPasswordChange() throws Exception{

        UserPasswordChangeDto userPasswordChangeDto = UserPasswordChangeDto.builder()
                .password("Testowanko123!")
                .passwordConfirm("Testowanko1234!")
                .build();

        UserSiteRenderDto userSiteRenderDto = UserSiteRenderDto.builder()
                .roles(new HashSet<>())
                .picUrl("")
                .lastName("")
                .id(UUID.randomUUID())
                .email("")
                .firstName("")
                .enabled(true)
                .build();

        this.mockMvc
                .perform(post("/dashboard/profile/password").sessionAttr("userData", userSiteRenderDto).with(csrf()).flashAttr("passwordChange", userPasswordChangeDto))
                .andExpect(view().name("userProfile"));
    }

    @Test
    @WithMockUser
    void shouldRedirectToProfileAfterReceivingFileUpload() throws Exception{

        MockMultipartFile profilePicture = new MockMultipartFile("picture_file", "filename.txt", "text/plain", "some data".getBytes());
        UUID userId = UUID.randomUUID();

        doNothing().when(userService).changeProfilePicture(profilePicture, userId);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dashboard/profile/picture")
                        .file(profilePicture).with(csrf())
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/dashboard/profile"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "Picture uploaded successfully!"));

        verify(userService, times(1)).changeProfilePicture(profilePicture, userId);
    }

    @Test
    @WithMockUser
    public void whenChangeProfilePictureShouldThrowIOException() throws Exception {

        MockMultipartFile profilePicture = new MockMultipartFile("picture_file", "filename.txt", "text/plain", "some data".getBytes());
        UUID userId = UUID.randomUUID();

        doThrow(new IOException("Failed to upload a file")).when(userService).changeProfilePicture(profilePicture, userId);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dashboard/profile/picture")
                        .file(profilePicture).with(csrf())
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/dashboard/profile"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "Failed to upload a file"));

        verify(userService, times(1)).changeProfilePicture(profilePicture, userId);
    }

    @Test
    @WithMockUser
    public void whenChangeProfilePictureShouldThrowUserNotFoundException() throws Exception {

        MockMultipartFile profilePicture = new MockMultipartFile("picture_file", "filename.txt", "text/plain", "some data".getBytes());
        UUID userId = UUID.randomUUID();

        doThrow(new UserNotFoundException("User not found")).when(userService).changeProfilePicture(profilePicture, userId);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dashboard/profile/picture")
                        .file(profilePicture).with(csrf())
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/dashboard/profile"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "User not found"));

        verify(userService, times(1)).changeProfilePicture(profilePicture, userId);
    }

    @Test
    @WithMockUser
    void shouldDisplayUserProfilePageWhenGivenValidRequest() throws Exception {
        Principal principal = () -> "test@test.com";
        UserSiteRenderDto userSiteRenderDto = UserSiteRenderDto.builder()
                .roles(new HashSet<>())
                .picUrl("")
                .lastName("")
                .id(UUID.randomUUID())
                .email("test@test.com")
                .firstName("")
                .enabled(true)
                .build();

        when(userService.getUserInfoByEmail(any(String.class))).thenReturn(userSiteRenderDto);

        mockMvc.perform(get("/dashboard/profile")
                        .principal(principal)
                        .flashAttr("message", "message")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("userProfile"))
                .andExpect(model().attributeExists("passwordChange"));
    }
}
