package ls.EmployeeWorkOrderManagment.security;

import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", username)));
    }

    private UserDetails createUserDetails(ls.EmployeeWorkOrderManagment.persistence.model.user.User userCredentials) {
        return User.builder()
                .username(userCredentials.getEmail())
                .password(userCredentials.getPassword())
                .disabled(!userCredentials.isEnabled())
                .roles(userCredentials.getRoles().stream().map(Role::getRoleName).toArray(String[]::new))
                .build();
    }
}
