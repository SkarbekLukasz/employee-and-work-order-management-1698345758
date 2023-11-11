package ls.EmployeeWorkOrderManagment.web.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;
import ls.EmployeeWorkOrderManagment.validation.PasswordMatcher;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PasswordMatcher
public class UserRegistrationDto {

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;
    @Email(message = "Wrong email address format")
    @NotNull
    private String email;
    @Size(max = 64, min = 8, message = "Your password must be at least 8 characters long")
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}", message = "Your password must contain at least 1 uppercase letter, 1 lowercase letter and 1 special character.")
    private String password;
    private String passwordConfirm;
}