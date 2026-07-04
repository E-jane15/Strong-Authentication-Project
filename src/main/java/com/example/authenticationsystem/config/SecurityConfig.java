package com.example.authenticationsystem.config;

import com.example.authenticationsystem.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          BCryptPasswordEncoder passwordEncoder) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/register",
                                "/verify-otp",
                                "/setup-authenticator",
                                "/css/**"
                        ).permitAll()

                        .anyRequest().authenticated()

                )

                // Your existing login
                .formLogin(form -> form

                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/verify-totp", true)
                        .failureUrl("/?error")
                        .permitAll()

                )

                // Keycloak login
                .oauth2Login(oauth -> oauth
                        .loginPage("/")
                        .defaultSuccessUrl("/dashboard", true)
                )

                .oauth2ResourceServer(resource ->
                        resource.jwt(Customizer.withDefaults()))

                .logout(logout -> logout

                        .logoutSuccessUrl("/")
                        .permitAll()

                );

        return http.build();
    }

}