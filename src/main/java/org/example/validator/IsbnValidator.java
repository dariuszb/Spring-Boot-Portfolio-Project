package org.example.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        return isbn.length() == 13;
    }
}
