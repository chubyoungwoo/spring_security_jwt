package com.cbw.security.jwt.global.exception.error;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException() {
        super();
    }
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidDataException(String message) {
        super(message);
    }
    public InvalidDataException(Throwable cause) {
        super(cause);
    }
}