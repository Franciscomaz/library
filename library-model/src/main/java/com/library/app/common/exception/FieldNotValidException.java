package com.library.app.common.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class FieldNotValidException extends RuntimeException{
    private String field;

    public FieldNotValidException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }

    @Override
    public String toString() {
        return "FieldNotValid{" +
                "field='" + field + '\'' +
                '}';
    }
}
