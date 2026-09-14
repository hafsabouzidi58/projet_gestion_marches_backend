package com.stage.gestion_marches.config;

import com.stage.gestion_marches.security.AuthTokenFilter;
import com.stage.gestion_marches.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints publics (Auth & système)
                        .requestMatchers("/api/auth/**", "/error").permitAll()

                        // 2. ADMIN_SYSTEME : Audit logs, gestion des utilisateurs & supervision globale

                        // 3. Consultation (GET) des Marchés & Prestataires pour RESPONSABLE_GREEN_WOOD
                        .requestMatchers(HttpMethod.GET, "/api/marches/**", "/api/prestataires/**")
                        .hasAnyAuthority(
                                "RESPONSABLE_GREEN_WOOD", "ROLE_RESPONSABLE_GREEN_WOOD",
                                "RESPONSABLE_MARCHES", "ROLE_RESPONSABLE_MARCHES",
                                "ADMIN_SYSTEME", "ROLE_ADMIN_SYSTEME"
                        )

                        // 4. Modification / Création (POST, PUT, DELETE) sur Marchés, Prestataires & Garanties
                        .requestMatchers("/api/marches/**", "/api/prestataires/**", "/api/garanties/**")
                        .hasAnyAuthority(
                                "RESPONSABLE_MARCHES", "ROLE_RESPONSABLE_MARCHES",
                                "ADMIN_SYSTEME", "ROLE_ADMIN_SYSTEME"
                        )

                        // 5. RESPONSABLE_GREEN_WOOD : Déclaration des problèmes, suivi des avancements & pénalités
                        .requestMatchers("/api/green-wood/**", "/api/problemes/**", "/api/avancements/**", "/api/penalites/**")
                        .hasAnyAuthority(
                                "RESPONSABLE_GREEN_WOOD", "ROLE_RESPONSABLE_GREEN_WOOD",
                                "ADMIN_SYSTEME", "ROLE_ADMIN_SYSTEME"
                        )

                        // Tout autre appel doit être authentifié
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}