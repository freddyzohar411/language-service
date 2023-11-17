package com.avensys.rts.languagesservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

import com.avensys.rts.languagesservice.annotation.ValidateString;

public class StringArrayValidator implements ConstraintValidator<ValidateString, String> {
    private String[] acceptedValues;

    @Override
    public void initialize(ValidateString constraintAnnotation) {
        this.acceptedValues = constraintAnnotation.acceptedValues();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;  // if the value is optional
        return Arrays.asList(acceptedValues).contains(value);
    }
}
