package org.apache.fineract.infrastructure.security.service;

import java.time.LocalDateTime;
import org.apache.fineract.infrastructure.core.service.PlatformEmailService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final AppUserRepositoryWrapper appUserRepository;

    private final AppUserRepository userRepository;
    private final PlatformEmailService emailService;

    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(AppUserRepositoryWrapper appUserRepository, AppUserRepository userRepository,
            PlatformEmailService emailService, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void sendPasswordResetEmail(String username) {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final AppUser appUser = appUserRepository.findByUsername(username);
        String password = new RandomPasswordGenerator(13).generate();
        final String encodePassword = this.passwordEncoder.encode(password);
        appUser.updateTemporaryPassword(encodePassword);
        // TODO allow number of hours from configuration
        appUser.updateTemporaryPasswordExpiryTime(LocalDateTime.now().plusHours(24));
        securityContext.setAuthentication(null);
        this.userRepository.saveAndFlush(appUser);
        this.emailService.sendToUserForgotPassword(appUser.getDisplayName(), appUser.getEmail(), appUser.getUsername(), password);
    }
}