package com.library.app.common.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class DuplicatedCategoryException extends RuntimeException {

    public DuplicatedCategoryException() {
        super();
    }
}
