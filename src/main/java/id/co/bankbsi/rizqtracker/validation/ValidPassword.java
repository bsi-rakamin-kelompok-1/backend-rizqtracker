package id.co.bankbsi.rizqtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character, and must be at least 8 characters long";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}