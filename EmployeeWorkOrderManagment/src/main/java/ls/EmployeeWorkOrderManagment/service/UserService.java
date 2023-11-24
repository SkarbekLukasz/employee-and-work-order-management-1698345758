package ls.EmployeeWorkOrderManagment.service;

import jakarta.transaction.Transactional;
import ls.EmployeeWorkOrderManagment.persistence.dao.RoleRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
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

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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
    public void enableUserAccount(VerificationToken token) throws InvalidTokenException, TokenExpiredException {
        User registeredUser = token.getUser();
        registeredUser.setEnabled(true);
        userRepository.save(registeredUser);
    }

    public User retrieveUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with this email not found"));
    }

    @Transactional
    public void changeUserPassword(UserPasswordChangeDto userRegistrationDto, ResetToken token) {
        User user = token.getUser();
        String newPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
