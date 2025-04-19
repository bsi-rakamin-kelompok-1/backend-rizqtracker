package id.co.bankbsi.rizqtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern
            .compile("^[1-9]\\d{1,3}\\d{6,14}$");

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }

        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}
