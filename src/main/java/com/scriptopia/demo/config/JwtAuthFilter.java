package com.scriptopia.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.dto.exception.ErrorResponse;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.io.IOException;
import java.security.SignatureException;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {


        System.out.println(">>> JwtAuthFilter 실행됨, path=" + req.getServletPath());

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            setErrorResponse(res, ErrorCode.E_400_MISSING_JWT);
            return;
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


            } catch (IllegalArgumentException e) {
                setErrorResponse(res,ErrorCode.E_400_MISSING_JWT);
                return;
            } catch (ExpiredJwtException e) {
                setErrorResponse(res,ErrorCode.E_401_EXPIRED_JWT);
                return;
            } catch (MalformedJwtException e) {
                setErrorResponse(res,ErrorCode.E_401_MALFORMED);
                return;
            } catch (UnsupportedJwtException e) {
                setErrorResponse(res,ErrorCode.E_401_UNSUPPORTED_JWT);
                return;
            } catch (JwtException e) {
                setErrorResponse(res,ErrorCode.E_401_INVALID_SIGNATURE);
                return;
            }

            chain.doFilter(req, res);
    }




    private void setErrorResponse(HttpServletResponse res, ErrorCode code) throws IOException {
        res.setStatus(code.getStatus().value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(res.getOutputStream(), new ErrorResponse(code));
    }
}