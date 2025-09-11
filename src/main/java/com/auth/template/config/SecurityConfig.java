package com.auth.template.config;

import com.auth.template.security.CustomUserDetailsService;
import com.auth.template.security.JwtAuthenticationEntryPoint;
import com.auth.template.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationEntryPoint authenticationEntryPoint, CorsConfigurationSource corsConfigurationSource, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    public static final String[] PUBLIC_URLS = {
            "/actuator/health",
            "/api/auth/register",
            "/api/auth/reset-password",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().configurationSource(corsConfigurationSource)
                .and()
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/api/auth/createRole","/api/auth/deleteRole/*","api/auth/createPermission","/api/auth/deletePermission/*")
                        .hasAnyAuthority("role_create","role_delete")
                        .requestMatchers("/api/auth/getAllRoles","/api/auth/getAllPermissions")
                        .hasAnyAuthority("role_view","role_delete","role_create")
                        .requestMatchers("/api/users")// get all user
                        .hasAnyAuthority("user_view","user_edit","user_delete","user_create","role_create","role_delete")
                        .requestMatchers(HttpMethod.DELETE,"/api/users/**").hasAnyAuthority("user_edit","user_delete","user_create","role_create","role_delete") // delete user
                        .requestMatchers(HttpMethod.PUT,"/api/users/**").hasAnyAuthority("user_edit","user_create","role_delete","role_create")//update user
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ✅ Replace wildcard with specific origins
        configuration.addAllowedOrigin("http://localhost:5000");
        configuration.addAllowedOrigin("http://98.130.134.68:44463");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
