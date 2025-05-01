package id.co.bankbsi.rizqtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PinValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPin {
    String message() default "Invalid PIN format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}