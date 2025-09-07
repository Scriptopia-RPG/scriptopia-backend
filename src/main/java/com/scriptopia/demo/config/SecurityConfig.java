package com.scriptopia.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.dto.exception.ErrorResponse;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.utils.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import java.nio.charset.StandardCharsets;
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

//    @Bean
//    public  JwtAuthFilter jwtAuthFilter(){
//        return new JwtAuthFilter(jwtProvider);
//    }

    @Bean
    @Order(1)
    public SecurityFilterChain authChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/auth/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain oAuthChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/oauth/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }



    @Bean
    @Order(99) // public 체인보다 뒤에서 동작
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(req -> {
                    var c = new CorsConfiguration();
                    c.setAllowedOrigins(List.of("http://localhost:3000"));
                    c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
                    c.setAllowedHeaders(List.of("Authorization","Content-Type"));
                    c.setAllowCredentials(true);
                    c.setMaxAge(3600L);
                    return c;
                }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // 나머지 전부 인증 필요
                )
                .addFilterBefore(new JwtAuthFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(ErrorCode.E_401.getStatus().value());
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            new ObjectMapper().writeValue(res.getOutputStream(),
                                    new ErrorResponse(ErrorCode.E_401));
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(E_403.getStatus().value());
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            new ObjectMapper().writeValue(res.getOutputStream(),
                                    new ErrorResponse(E_403));
                        })
                );
        return http.build();
    }
}