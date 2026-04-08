package com.project.kisan_setu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider(){
        return ()-> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() ||
                    auth instanceof AnonymousAuthenticationToken) {
                return Optional.of("system_user");
            }
            String auditor = auth.getName();
            if (auditor == null || auditor.isBlank() || "anonymousUser".equalsIgnoreCase(auditor)) {
                return Optional.of("system_user");
            }
            return Optional.of(auditor);
        };
    }
}
