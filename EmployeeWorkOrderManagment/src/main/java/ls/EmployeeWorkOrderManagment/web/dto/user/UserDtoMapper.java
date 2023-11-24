package ls.EmployeeWorkOrderManagment.web.dto.user;

import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;

import java.util.stream.Collectors;

public class UserDtoMapper {

    public static UserCredentialsDto mapCredentialsToDto(User user) {
        return UserCredentialsDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .enabled(user.isEnabled())
                .roles(
                        user.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toSet())
                )
                .build();
    }

    public static UserSiteRenderDto mapSiteRenderToDto(User user) {
        return UserSiteRenderDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
