package id.co.bankbsi.rizqtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PinValidator implements ConstraintValidator<ValidPin, String> {
    private static final Pattern PIN_PATTERN = Pattern.compile("^[0-9]{6}$");

    @Override
    public boolean isValid(String pin, ConstraintValidatorContext context) {
        if (pin == null || pin.isEmpty()) {
            return true;
        }

        boolean isValid = PIN_PATTERN.matcher(pin).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("PIN must be exactly 6 digits")
                    .addConstraintViolation();
        }

        return isValid;
    }
}