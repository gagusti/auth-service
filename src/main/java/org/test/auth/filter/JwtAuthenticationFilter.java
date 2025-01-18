package org.test.auth.filter;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.test.auth.exception.UserNotFoundException;
import org.test.auth.model.User;
import org.test.auth.security.JwtManager;
import org.test.auth.service.UserService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final UserService userService;
    private final JwtManager jwtManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                email = jwtManager.extractEmail(jwtToken);
            } catch (Exception e) {
                logger.error("Error extracting username from token: " + e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            try {
                Optional<User> user = userService.loadUserByEmail(email);
                userDetails = new org.springframework.security.core.userdetails.User(
                        user.get().getEmail(),
                        user.get().getPassword(),
                        new ArrayList<>()
                );
            } catch(UserNotFoundException ex) {
                logger.error("User email not found in the Authentication");
            }

            try {
                if (jwtManager.isTokenValid(email, jwtToken)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch(Exception ex) {
                logger.error("Authentication was not able to validate the provide token");
            }

        }
        filterChain.doFilter(request, response);
    }
}
