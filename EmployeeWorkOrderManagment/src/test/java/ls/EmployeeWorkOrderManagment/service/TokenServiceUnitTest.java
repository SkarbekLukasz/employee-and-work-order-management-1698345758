package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.persistence.dao.ResetTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.VerificationTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.TokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceUnitTest {

    @Mock
    ResetTokenRepository resetTokenRepository;

    @Mock
    VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    TokenService tokenService;

    private User commonUser;

    @BeforeEach
    void setUpObjects() {
        UUID id = UUID.randomUUID();
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(UUID.randomUUID(), "USER"));
        commonUser = User.builder().id(id).email("email").lastName("last").firstName("first").roles(roles).password("password").enabled(true).build();
    }

    @Test
    void shouldSaveNewResetToken() {
        //given
        String token = "sometesttoken";

        //when
        tokenService.createNewResetToken(commonUser, token);

        //then
        verify(resetTokenRepository, times(1)).save(any(ResetToken.class));
    }

    @Test
    void shouldSaveNewVerificationToken() {
        //given
        String token = "sometesttoken";

        //when
        tokenService.createNewVerificationToken(commonUser, token);

        //then
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenResetTokenInvalid() {
        //given
        String token = "sampletoken";
        when(resetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(InvalidTokenException.class, () -> tokenService.confirmResetToken(token));
    }

    @Test
    void shouldThrowTokenExpiredExceptionWhenResetTokenIsExpired() {
        //given
        String token = "sampletoken";
        ResetToken mockResetToken = new ResetToken(token);
        long expiredTime = 60 * 24 * 1000;
        mockResetToken.setExpiryDate(new Date(System.currentTimeMillis() - 1000));
        when(resetTokenRepository.findByToken(token)).thenReturn(Optional.of(mockResetToken));

        //when
        //then
        assertThrows(TokenExpiredException.class, () -> tokenService.confirmResetToken(token));
    }

    @Test
    void shouldReturnValidResetToken() {
        //given
        String token = "sampletoken";
        ResetToken mockResetToken = new ResetToken(token);
        when(resetTokenRepository.findByToken(token)).thenReturn(Optional.of(mockResetToken));

        //when
        ResetToken result = tokenService.confirmResetToken(token);

        //then
        assertEquals(mockResetToken, result);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenVerificationTokenInvalid() {
        //given
        String token = "sampletoken";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(InvalidTokenException.class, () -> tokenService.confirmUserRegistration(token));
    }

    @Test
    void shouldThrowTokenExpiredExceptionWhenVerificationTokenIsExpired() {
        //given
        String token = "sampletoken";
        VerificationToken mockVerificationToken = new VerificationToken(token);
        long expiredTime = 60 * 24 * 1000;
        mockVerificationToken.setExpiryDate(new Date(System.currentTimeMillis() - 1000));
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(mockVerificationToken));

        //when
        //then
        assertThrows(TokenExpiredException.class, () -> tokenService.confirmUserRegistration(token));
    }

    @Test
    void shouldReturnValidVerificationToken() {
        //given
        String token = "sampletoken";
        VerificationToken mockVerificationToken = new VerificationToken(token);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(mockVerificationToken));

        //when
        VerificationToken result = tokenService.confirmUserRegistration(token);

        //then
        assertEquals(mockVerificationToken, result);
    }

}