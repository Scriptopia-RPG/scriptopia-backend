package com.scriptopia.demo.config;

import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.utils.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.io.IOException;
import java.security.SignatureException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        if (uri.startsWith("/api/v1/public")) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.E_400_MISSING_JWT);
        }

            String token = authHeader.substring(7);
            try {
                jwt.parse(token); // 유효성 체크

                String userId = jwt.getUserId(token).toString();
                var roles = jwt.getRoles(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var authentication = new UsernamePasswordAuthenticationToken(userId, null, roles);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                throw new CustomException(ErrorCode.E_401_EXPIRED_JWT);
            } catch (MalformedJwtException e) {
                throw new CustomException(ErrorCode.E_401_MALFORMED);
            } catch (UnsupportedJwtException e) {
                throw new CustomException(ErrorCode.E_401_UNSUPPORTED_JWT);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.E_400_MISSING_JWT);
            } catch (JwtException e) {
                throw new CustomException(ErrorCode.E_401_INVALID_SIGNATURE);
            }

            chain.doFilter(req, res);
    }
}