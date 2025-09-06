package com.springproject.stbookingsystem.exception.custom;

/**
 * 전화번호 중복 예외
 */
public class PhoneAlreadyExistsException extends BusinessException {
    public PhoneAlreadyExistsException(String phone) {
        super("PHONE_ALREADY_EXISTS", "이미 존재하는 전화번호입니다: " + phone);
    }
}
