package ls.EmployeeWorkOrderManagment.web.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ls.EmployeeWorkOrderManagment.validation.ResetPasswordMatcher;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ResetPasswordMatcher
public class UserPasswordChangeDto {

    @Size(max = 64, min = 8, message = "Your password must be at least 8 characters long")
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}", message = "Your password must contain at least 1 uppercase letter, 1 lowercase letter and 1 special character.")
    private String password;
    private String passwordConfirm;
}
