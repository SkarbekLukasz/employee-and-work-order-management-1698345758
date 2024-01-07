package ls.EmployeeWorkOrderManagment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Constraint(validatedBy={UUIDMatcherValidator.class})
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface UUIDMatcher {
    String message() default "Invalid designer ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}