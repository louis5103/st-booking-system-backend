package com.springproject.stbookingsystem.exception.custom;

/**
 * 이메일 중복 예외
 */
public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다: " + email);
    }
}
