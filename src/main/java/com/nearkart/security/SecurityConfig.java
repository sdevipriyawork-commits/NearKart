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

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
    // CORS CONFIGURATION
    // =========================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
                List.of("*")
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =========================
    // SECURITY CONFIGURATION
    // =========================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =========================
                // CORS
                // =========================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // =========================
                // CSRF
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
                        // OPTIONS
                        // =========================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // =========================
                        // ROOT
                        // =========================

                        .requestMatchers(
                                "/",
                                "/error"
                        ).permitAll()

                        // =========================
                        // AUTH
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
                        // USER - GET PROFILE
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "CUSTOMER",
                                "SHOP_OWNER",
                                "DELIVERY_PARTNER",
                                "USER"
                        )

                        // =========================
                        // USER - UPDATE PROFILE
                        // =========================

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/**"
                        ).authenticated()

                        // =========================
                        // CATEGORY MANAGEMENT
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

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )

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

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/shops/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/shops/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/shops/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SHOP_OWNER"
                        )

                        // =========================
                        // CART
                        // =========================

                        .requestMatchers(
                                "/api/cart/**"
                        ).authenticated()

                        // =========================
                        // ORDERS
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
                // JWT FILTER
                // =========================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}