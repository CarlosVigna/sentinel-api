package com.sentinel.config;

import com.sentinel.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/auth/**").permitAll()

                                .requestMatchers(HttpMethod.GET, "/categories/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/protocols/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/protocols/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/protocols/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/protocols/**").hasRole("ADMIN")

                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}