package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.EmployeeWorkOrderManagmentApplication;
import ls.EmployeeWorkOrderManagment.persistence.dao.ResetTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.VerificationTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest(classes = {
        EmployeeWorkOrderManagmentApplication.class
})
@Transactional
@TestPropertySource(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15.2-alpine://db"
})
@Testcontainers
public class TokenServiceIntegrationTest {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    ResetTokenRepository resetTokenRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    private User commonUser;

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
    void shouldSaveGivenResetToken() {
        //given
        String token = UUID.randomUUID().toString();

        //when
        tokenService.createNewResetToken(commonUser, token);

        //then
        ResetToken fetchedToken = resetTokenRepository.findByToken(token).get();
        Assertions.assertEquals(commonUser.getEmail(), fetchedToken.getUser().getEmail());
    }

    @Test
    @Sql("classpath:/sql/schema.sql")
    void shouldSaveGivenVerificationToken() {
        //given
        String token = UUID.randomUUID().toString();

        //when
        tokenService.createNewVerificationToken(commonUser, token);

        //then
        VerificationToken fetchedToken = verificationTokenRepository.findByToken(token).get();
        Assertions.assertEquals(commonUser.getEmail(), fetchedToken.getUser().getEmail());
    }
}
