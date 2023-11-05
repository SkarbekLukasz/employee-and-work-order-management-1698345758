package ls.EmployeeWorkOrderManagment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;

public class PasswordMatcherValidator implements ConstraintValidator<PasswordMatcher, Object> {
    @Override
    public void initialize(PasswordMatcher constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        UserRegistrationDto user = (UserRegistrationDto) o;
        return user.getPassword().equals(user.getPasswordConfirm());
    }
}
