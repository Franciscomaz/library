package com.library.app.common.exception;

public class InvalidJson extends RuntimeException {

    public InvalidJson(String message) {
        super(message);
    }

    public InvalidJson(Throwable cause) {
        super(cause);
    }
}
