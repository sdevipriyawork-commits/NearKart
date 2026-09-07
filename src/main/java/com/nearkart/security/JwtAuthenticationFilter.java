package com.nearkart.security;

import com.nearkart.entity.User;
import com.nearkart.repository.UserRepository;
import com.nearkart.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    // =========================
    // JWT FILTER
    // =========================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // GET AUTHORIZATION HEADER
        String authHeader =
                request.getHeader("Authorization");


        // CHECK JWT TOKEN
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        // REMOVE "Bearer "
        String token = authHeader.substring(7);


        try {

            // EXTRACT EMAIL
            String email =
                    jwtService.extractEmail(token);


            // CHECK USER
            if (email != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {


                User user = userRepository
                        .findByEmail(email)
                        .orElse(null);


                // VALIDATE TOKEN
                if (user != null &&
                        jwtService.isTokenValid(token, user)) {


                    // =========================
                    // CREATE USER ROLE
                    // =========================

                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority(
                                    "ROLE_" + user.getRole()
                            );


                    // =========================
                    // CREATE AUTHENTICATION
                    // =========================

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(authority)
                            );


                    // SET REQUEST DETAILS
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    // SAVE AUTHENTICATION
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception e) {

            // INVALID TOKEN
            SecurityContextHolder.clearContext();
        }


        // CONTINUE REQUEST
        filterChain.doFilter(request, response);
    }
}