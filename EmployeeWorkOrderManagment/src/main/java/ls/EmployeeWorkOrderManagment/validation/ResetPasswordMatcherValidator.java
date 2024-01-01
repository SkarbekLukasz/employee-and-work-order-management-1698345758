package ls.EmployeeWorkOrderManagment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;

public class ResetPasswordMatcherValidator implements ConstraintValidator<ResetPasswordMatcher, Object> {

    @Override
    public void initialize(ResetPasswordMatcher constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        UserPasswordChangeDto user = (UserPasswordChangeDto) o;
        return user.getPassword().equals(user.getPasswordConfirm());
    }
}
