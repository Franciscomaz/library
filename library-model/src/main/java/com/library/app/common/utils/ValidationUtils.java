package com.library.app.common.utils;

import com.library.app.common.exception.FieldNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

public class ValidationUtils {
    public static<T> void validateFields(final T entity, Validator validator) {
        final Set<ConstraintViolation<T>> errors = validator.validate(entity);
        final Iterator<ConstraintViolation<T>> itErrors = errors.iterator();

        if (itErrors.hasNext()) {
            ConstraintViolation<T> violation = itErrors.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }
}
