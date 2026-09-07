package com.nearkart.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    // =========================
    // CONSTRUCTOR
    // =========================

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // =========================
    // PASSWORD ENCODER
    // =========================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =========================
    // SECURITY CONFIGURATION
    // =========================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =========================
                // DISABLE CSRF
                // =========================

                .csrf(csrf -> csrf.disable())


                // =========================
                // STATELESS SESSION
                // =========================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =========================
                // DISABLE DEFAULT LOGIN
                // =========================

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())


                // =========================
                // API PERMISSIONS
                // =========================

                .authorizeHttpRequests(auth -> auth


                        // =========================
                        // ROOT PAGE - PUBLIC
                        // =========================

                        .requestMatchers(
                                "/",
                                "/error"
                        ).permitAll()


                        // =========================
                        // AUTH APIs - PUBLIC
                        // =========================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // =========================
                        // PUBLIC GET APIs
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**",
                                "/api/shops/**",
                                "/api/categories/**"
                        ).permitAll()


                        // =========================
                        // USERS - ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // CATEGORY MANAGEMENT
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // PRODUCT MANAGEMENT
                        // =========================

                        // CREATE PRODUCT

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )


                        // UPDATE PRODUCT

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )


                        // DELETE PRODUCT

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )


                        // =========================
                        // SHOP MANAGEMENT
                        // =========================

                        // CREATE SHOP

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/shops/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )


                        // UPDATE SHOP

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/shops/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )


                        // DELETE SHOP

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/shops/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )


                        // =========================
                        // CART - LOGIN REQUIRED
                        // =========================

                        .requestMatchers(
                                "/api/cart/**"
                        ).authenticated()


                        // =========================
                        // ORDERS - LOGIN REQUIRED
                        // =========================

                        .requestMatchers(
                                "/api/orders/**"
                        ).authenticated()


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )


                // =========================
                // ADD JWT FILTER
                // =========================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}