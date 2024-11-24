package com.chili.exception;

public class UserAlreadyException extends BaseException {
    public UserAlreadyException() {
    }

    public UserAlreadyException(String message) {
        super(message);
    }
}
