    package org.tpjava.AuthService.configuration;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.tpjava.AuthService.security.JwtFilter;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        private final JwtFilter jwtFilter;

        public SecurityConfig(JwtFilter jwtFilter) {
            this.jwtFilter = jwtFilter;
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

            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            System.out.println("SecurityFilterChain configuration complete.");
            return http.build();
        }
    }
