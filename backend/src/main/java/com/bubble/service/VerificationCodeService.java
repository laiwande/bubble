package com.bubble.service;

public interface VerificationCodeService {
    String generateAndSaveCode(String email);

    boolean verifyCode(String email, String code);
}
