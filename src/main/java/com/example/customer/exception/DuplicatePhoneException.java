package com.example.customer.exception;

public class DuplicatePhoneException extends RuntimeException {

    public DuplicatePhoneException(String phone) {
        super("手机号已存在: " + phone);
    }

    public DuplicatePhoneException(String message, Throwable cause) {
        super(message, cause);
    }
}
