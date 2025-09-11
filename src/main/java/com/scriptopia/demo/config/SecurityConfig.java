package com.scriptopia.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.dto.exception.ErrorResponse;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

import static com.scriptopia.demo.exception.ErrorCode.E_403;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        //public 권한
                        .requestMatchers(
                                SecurityWhitelist.AUTH_WHITELIST
                        ).permitAll()

                        //user 권한
                        .requestMatchers(
                                "/auth/password/change"
                        ).hasAuthority("USER")

                        //admin 권한
                        .requestMatchers(
                                ""
                        ).hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}