package org.apache.fineract.infrastructure.security.service;

public interface PasswordService {

    void sendPasswordResetEmail(String username);
}
