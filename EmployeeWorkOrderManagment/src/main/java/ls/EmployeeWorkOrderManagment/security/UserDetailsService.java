package ls.EmployeeWorkOrderManagment.security;

import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserCredentialsDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserCredentialsDtoMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return  userRepository.findByEmail(username).map(UserCredentialsDtoMapper::mapToDto)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", username)));

    }

    private UserDetails createUserDetails(UserCredentialsDto userCredentialsDto) {
        return User.builder()
                .username(userCredentialsDto.email())
                .password(userCredentialsDto.password())
                .roles(userCredentialsDto.roles().toArray(String[]::new))
                .build();
    }
}
