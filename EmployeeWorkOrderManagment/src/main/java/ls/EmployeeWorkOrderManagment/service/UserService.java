package ls.EmployeeWorkOrderManagment.service;

import ls.EmployeeWorkOrderManagment.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.dto.UserCredentialsDto;
import ls.EmployeeWorkOrderManagment.dto.UserCredentialsDtoMapper;
import ls.EmployeeWorkOrderManagment.model.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserCredentialsDto> retrieveCredentialsByEmail(String email) {
        return userRepository.findByEmail(email).map(UserCredentialsDtoMapper::mapToDto);
    }
}
