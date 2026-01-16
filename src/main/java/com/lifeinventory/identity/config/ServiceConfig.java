package com.lifeinventory.identity.config;

import com.lifeinventory.identity.event.IdentityEventPublisher;
import com.lifeinventory.identity.repository.TokenRepository;
import com.lifeinventory.identity.repository.UserRepository;
import com.lifeinventory.identity.service.AuthenticationService;
import com.lifeinventory.identity.service.PasswordHasher;
import com.lifeinventory.identity.service.TokenGenerator;
import com.lifeinventory.identity.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            PasswordHasher passwordHasher,
            TokenGenerator tokenGenerator,
            IdentityEventPublisher eventPublisher
    ) {
        return new UserService(
                userRepository,
                tokenRepository,
                passwordHasher,
                tokenGenerator,
                eventPublisher
        );
    }

    @Bean
    public AuthenticationService authenticationService(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            PasswordHasher passwordHasher,
            TokenGenerator tokenGenerator,
            IdentityEventPublisher eventPublisher
    ) {
        return new AuthenticationService(
                userRepository,
                tokenRepository,
                passwordHasher,
                tokenGenerator,
                eventPublisher
        );
    }
}
