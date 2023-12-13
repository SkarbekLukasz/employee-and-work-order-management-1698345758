package ls.EmployeeWorkOrderManagment.web.dto.user;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserSiteRenderDto(String firstName,
                                String lastName,
                                String email,
                                boolean enabled,
                                Set<String> roles,
                                UUID id,
                                String picUrl) {
}
