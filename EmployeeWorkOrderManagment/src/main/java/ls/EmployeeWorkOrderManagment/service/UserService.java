package ls.EmployeeWorkOrderManagment.service;

import jakarta.transaction.Transactional;
import ls.EmployeeWorkOrderManagment.persistence.dao.ResetTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.RoleRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.VerificationTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.Token;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.TokenExpiredException;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ResetTokenRepository resetTokenRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       VerificationTokenRepository verificationTokenRepository, ResetTokenRepository resetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.resetTokenRepository = resetTokenRepository;
    }

    @Transactional
    public User saveNewUserAccount(UserRegistrationDto userRegistrationData) throws UserAlreadyExistsException, NoSuchElementException {
        if(isEmailUnique(userRegistrationData.getEmail())) {
            throw new UserAlreadyExistsException("There is already an account with that email address: " + userRegistrationData.getEmail());
        }
        User user = new User();
        user.setEmail(userRegistrationData.getEmail());
        String hashedPassword = passwordEncoder.encode(userRegistrationData.getPassword());
        user.setPassword(hashedPassword);
        user.setFirstName(userRegistrationData.getFirstName());
        user.setLastName(userRegistrationData.getLastName());
        Optional<Role> role = roleRepository.findByRoleName("USER");
        role.ifPresentOrElse(
                user::addRoles,
                () -> {
                    throw new NoSuchElementException("Failed to create new account");
                }
        );
        return userRepository.save(user);
    }

    private boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void createNewVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void createNewResetToken(User user, String token) {
        ResetToken resetToken = new ResetToken(token);
        resetToken.setUser(user);
        resetTokenRepository.save(resetToken);
    }

    @Transactional
    public void confirmUserRegistration(String token) throws InvalidTokenException, TokenExpiredException {
        Optional<VerificationToken> fetchedToken = verificationTokenRepository.findByToken(token);
        if(fetchedToken.isEmpty()) throw new InvalidTokenException("Provided token is invalid.");

        VerificationToken verificationToken = fetchedToken.get();
        if(isTokenExpired(verificationToken)) throw new TokenExpiredException("Provided token has expired.");

        User registeredUser = verificationToken.getUser();
        registeredUser.setEnabled(true);
        userRepository.save(registeredUser);
    }

    private boolean isTokenExpired(Token token) {
        Calendar cal = Calendar.getInstance();
        return token.getExpiryDate().getTime() - cal.getTime().getTime() <= 0;
    }

    public void confirmResetToken(String token) {
        Optional<ResetToken> fetchedToken = resetTokenRepository.findByToken(token);
        if(fetchedToken.isEmpty()) throw new InvalidTokenException("Provided token is invalid");

        ResetToken resetToken = fetchedToken.get();
        if(isTokenExpired(resetToken)) throw new TokenExpiredException("Provided token has expired.");
    }

    public User retrieveUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with this email not found"));
    }

    public void changeUserPassword(UserPasswordChangeDto userRegistrationDto, String token) {
        Optional<ResetToken> resetToken = resetTokenRepository.findByToken(token);
        User user = resetToken.get().getUser();
        String newPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
