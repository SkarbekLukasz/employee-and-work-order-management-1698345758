package ls.EmployeeWorkOrderManagment.dto;

import ls.EmployeeWorkOrderManagment.model.role.Role;
import ls.EmployeeWorkOrderManagment.model.user.User;

import java.util.stream.Collectors;

public class UserCredentialsDtoMapper {

    public static UserCredentialsDto mapToDto(User user) {
        return UserCredentialsDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(
                        user.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
