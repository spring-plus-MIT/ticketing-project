package com.example.ticketingproject.security.jwt;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.exception.CustomJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {
                Claims claims = jwtTokenProvider.getClaims(token);

                Long userId = claims.get("userId", Long.class);
                String email = claims.getSubject();
                UserRole role = UserRole.valueOf(claims.get("role", String.class));

                CustomUserDetails customUserDetails =
                        new CustomUserDetails(userId, email, role);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    customUserDetails,
                                    null,
                                    customUserDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {

                throw new CustomJwtException(
                        ErrorStatus.TOKEN_EXPIRED.getHttpStatus(),
                        ErrorStatus.TOKEN_EXPIRED);

            } catch (SignatureException e) {

                throw new CustomJwtException(
                        ErrorStatus.INVALID_SIGNATURE.getHttpStatus(),
                        ErrorStatus.INVALID_SIGNATURE);

            } catch (MalformedJwtException e) {

                throw new CustomJwtException(
                        ErrorStatus.INVALID_TOKEN.getHttpStatus(),
                        ErrorStatus.INVALID_TOKEN);

            } catch (Exception e) {

                throw new CustomJwtException(
                        ErrorStatus.INVALID_TOKEN.getHttpStatus(),
                        ErrorStatus.INVALID_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }
}
