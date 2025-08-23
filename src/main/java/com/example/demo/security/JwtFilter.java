package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        logger.debug("Incoming request {} {}", request.getMethod(), request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.debug("Found Authorization header, token length = {}", token.length());

            try {
                // Returns Optional.empty() if token invalid/expired
                var maybeUsername = jwtUtil.getUsernameIfTokenValid(token);

                if (maybeUsername.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String username = maybeUsername.get();

                    // If you later want roles in token, parse them and populate authorities here.
                    // For now, give a default ROLE_USER so .authenticated() endpoints pass and role checks work.
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("JWT validated, authentication set for user: {}", username);
                } else {
                    if (maybeUsername.isEmpty()) {
                        logger.debug("Token invalid or expired.");
                    }
                }
            } catch (Exception ex) {
                // catch-any to avoid breaking the filter chain with an exception
                logger.warn("Error validating token: {}", ex.getMessage());
            }
        } else {
            logger.debug("No Authorization header or does not start with Bearer");
        }

        filterChain.doFilter(request, response);
    }
}
