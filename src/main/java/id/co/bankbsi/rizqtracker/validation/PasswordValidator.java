package id.co.bankbsi.rizqtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (password == null || password.isEmpty()) {
            context.buildConstraintViolationWithTemplate("Password is required")
                    .addConstraintViolation();
            return false;
        }

        if (password.length() < 8) {
            context.buildConstraintViolationWithTemplate("Minimum password length is 8 characters")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one symbol character")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}