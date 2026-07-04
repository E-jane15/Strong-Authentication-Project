package com.example.authenticationsystem.config;

import com.example.authenticationsystem.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            BCryptPasswordEncoder passwordEncoder,
            Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
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

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )

                // Username/password login
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/verify-totp", true)
                        .failureUrl("/?error")
                        .permitAll()
                )

                // Keycloak Login
                .oauth2Login(oauth -> oauth
                        .loginPage("/")
                        .defaultSuccessUrl("/dashboard", true)
                )

                // Resource Server
                .oauth2ResourceServer(resource ->
                        resource.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}