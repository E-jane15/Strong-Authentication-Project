package com.example.authenticationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
public class JwtConverter {

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {

        return jwt -> {

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

            // Extract scopes
            List<String> scopes = jwt.getClaimAsStringList("scope");

            if (scopes != null) {
                scopes.forEach(scope ->
                        authorities.add(
                                new SimpleGrantedAuthority("SCOPE_" + scope)
                        )
                );
            }

            // Extract Keycloak realm roles
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess != null) {

                List<String> roles =
                        (List<String>) realmAccess.get("roles");

                if (roles != null) {
                    roles.forEach(role ->
                            authorities.add(
                                    new SimpleGrantedAuthority("ROLE_" + role)
                            )
                    );
                }
            }

            return new JwtAuthenticationToken(jwt, authorities);
        };
    }
}
