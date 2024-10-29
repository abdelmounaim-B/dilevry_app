package org.tpjava.AuthService.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.tpjava.AuthService.security.JwtFilter;
import org.tpjava.AuthService.security.ServiceApiTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ServiceApiTokenFilter serviceApiTokenFilter;

    public SecurityConfig(JwtFilter jwtFilter, ServiceApiTokenFilter serviceApiTokenFilter) {
        this.jwtFilter = jwtFilter;
        this.serviceApiTokenFilter = serviceApiTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/employees/internal/validateEmployee", "/public/**").permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        // Add ServiceApiTokenFilter to validate the Service API Token
        http.addFilterBefore(serviceApiTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // Add JwtFilter for JWT validation after the Service API Token is validated
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
