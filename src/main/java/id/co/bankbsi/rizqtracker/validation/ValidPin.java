package id.co.bankbsi.rizqtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PinValidator.class)  // Point to custom validator
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPin {
    String message() default "Invalid PIN format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}