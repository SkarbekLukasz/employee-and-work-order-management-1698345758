package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.EmployeeWorkOrderManagmentApplication;
import ls.EmployeeWorkOrderManagment.persistence.dao.RoleRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

@SpringBootTest(classes = {
        EmployeeWorkOrderManagmentApplication.class
})
@Transactional
@TestPropertySource(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15.2-alpine://db"
})
@Testcontainers
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    public User commonUser;

    @BeforeEach
    void setUpObjects() {
        UUID id = UUID.randomUUID();
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(UUID.randomUUID(), "USER"));
        commonUser = User.builder().id(id).email("test@test.com").lastName("last").firstName("first").roles(roles).password("$2a$12$j/hhBGn4q/n6Q9lyms8zmODeh9uxdh0aP60Mo2HPyclNW1JXjoCyq").enabled(true).picUrl("").build();
        userRepository.save(commonUser);
    }
    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldDeleteOneUserAccount() {
        //given
        String id = commonUser.getId().toString();

        //when
        userService.deleteUserAccount(id);

        //then
        Assertions.assertEquals(userRepository.findById(commonUser.getId()), Optional.empty());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldReturnListWithOneUser() {
        //given
        //when
        List<UserSiteRenderDto> users = userService.getAllUsersInfo();

        //then
        Assertions.assertEquals(users.size(), 1);
        Assertions.assertFalse(users.isEmpty());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldChangeUserPasswordToGivenOne() {
        //given
        ResetToken resetToken = new ResetToken(UUID.randomUUID().toString());
        resetToken.setUser(commonUser);
        UserPasswordChangeDto userPasswordChangeDto = new UserPasswordChangeDto("Testowanko123!", "Testowanko123!");

        //when
        userService.resetUserPassword(userPasswordChangeDto, resetToken);

        //then
        Assertions.assertTrue(passwordEncoder.matches("Testowanko123!", userRepository.findById(commonUser.getId()).get().getPassword()));
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldReturnUserWhenGivenValidEmail() {
        //given
        String email = commonUser.getEmail();

        //when
        User user = userService.retrieveUserByEmail(email);

        //then
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getEmail(), commonUser.getEmail());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldThrowUsernameNotFoundExceptionWhenGivenInvalidEmail() {
        //given
        String email = "jan@kowalski.com";

        //when
        //then
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.retrieveUserByEmail(email));
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldEnableCommonUserAccountWhenGivenValidToken() {
        //given
        UUID id = UUID.randomUUID();
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(UUID.randomUUID(), "USER"));
        User unCommonUser = User.builder().id(id).email("test2@test.com").lastName("last").firstName("first").roles(roles).password("$2a$12$j/hhBGn4q/n6Q9lyms8zmODeh9uxdh0aP60Mo2HPyclNW1JXjoCyq").enabled(false).picUrl("").build();
        userRepository.save(unCommonUser);
        VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString());
        verificationToken.setUser(unCommonUser);

        //when
        userService.enableUserAccount(verificationToken);

        //then
        Assertions.assertTrue(userRepository.findById(verificationToken.getUser().getId()).get().isEnabled());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldThrowUserAlreadyExistsExceptionWithGivenExistingEmail() {
        //given
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .email("test@test.com")
                .firstName("first")
                .lastName("last")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();

        //when
        //then
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.saveNewUserAccount(userRegistrationDto));
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldThrowNoSuchElementExceptionWhenThereIsNoUserRole() {
        //given
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .email("test2@test.com")
                .firstName("first")
                .lastName("last")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        System.out.println(roleRepository.findAll());

        //when
        //then
        Assertions.assertThrows(NoSuchElementException.class, () -> userService.saveNewUserAccount(userRegistrationDto));
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldSaveGivenUserIntoDatabase() {
        //given
       UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .email("test3@test.com")
                .firstName("first")
                .lastName("last")
                .password("Testowanko123!")
                .passwordConfirm("Testowanko123!")
                .build();

        //when
        User savedUser = userService.saveNewUserAccount(userRegistrationDto);

        //then
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(userRegistrationDto.getEmail(), savedUser.getEmail());
        Assertions.assertEquals(userRegistrationDto.getFirstName(), savedUser.getFirstName());
        Assertions.assertEquals(userRegistrationDto.getLastName(), savedUser.getLastName());
        Assertions.assertTrue(passwordEncoder.matches(userRegistrationDto.getPassword(), savedUser.getPassword()));
        Assertions.assertFalse(savedUser.isEnabled());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldReturnValidUserSiteRenderData() {
        //given
        String email = "test@test.com";

        //when
        UserSiteRenderDto userSiteRenderDto = userService.getUserInfoByEmail(email);

        //then
        Assertions.assertEquals(commonUser.getId(), userSiteRenderDto.id());
        Assertions.assertNotNull(userSiteRenderDto);
        Assertions.assertEquals(commonUser.getEmail(), email);
        Assertions.assertTrue(commonUser.isEnabled());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldThrowUserNotFoundExceptionForGivenEmail() {
        //given
        String email = "pies@kot.com";

        //when
        //then
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserInfoByEmail(email));
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldThrowUserNotFoundExceptionForGivenUserUUID() {
        //given
        UUID id = UUID.randomUUID();
        boolean enable = true;
        Set<UUID> roles = new HashSet<>();

        //when
        //then
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.editUserAccount(roles, enable, id));
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldSaveOnlyUserRoleIntoGivenUserEntity() {
        //given
        UUID id = commonUser.getId();
        Set<UUID> roles = new HashSet<>();
        Role role = new Role();
        role.setRoleName("Tester");
        role =  roleRepository.save(role);
        roles.add(role.getId());
        boolean enable = true;

        //when
        userService.editUserAccount(roles, enable, id);

        //then
        Assertions.assertEquals(1, userRepository.findById(id).get().getRoles().size());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldChangeUserFirstName() {
        //given
        String email = "test@test.com";
        String firstName = "last";

        //when
        userService.changeUserFirstName(firstName, email);

        //then
        Assertions.assertEquals(firstName, userRepository.findById(commonUser.getId()).get().getFirstName());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldChangeUserLastName() {
        //given
        String email = "test@test.com";
        String lastName = "first";

        //when
        userService.changeUserLastName(lastName, email);

        //then
        Assertions.assertEquals(lastName, userRepository.findById(commonUser.getId()).get().getLastName());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldChangeUserPasswordToProvidedOne() {
        //given
        UserPasswordChangeDto userPasswordChangeDto = new UserPasswordChangeDto("Testowanko123!", "Testowanko123!");
        String email = commonUser.getEmail();

        //when
        userService.changeUserPassword(userPasswordChangeDto, email);

        //then
        Assertions.assertTrue(passwordEncoder.matches(userPasswordChangeDto.getPassword(), userRepository.findById(commonUser.getId()).get().getPassword()));
    }
}
