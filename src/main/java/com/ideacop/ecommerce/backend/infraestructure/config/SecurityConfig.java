package com.ideacop.ecommerce.backend.infraestructure.config;

import com.ideacop.ecommerce.backend.infraestructure.jwt.JWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(JWTAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(
                        cors -> cors.configurationSource(
                                request -> {
                                    CorsConfiguration corsConfiguration= new CorsConfiguration();
                                    corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3200", "http://localhost:3000"));
                                    corsConfiguration.setAllowedMethods(Arrays.asList("*"));
                                    corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
                                    return corsConfiguration;
                                }
                        )
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                aut -> aut
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/admin/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/orders")).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/orders/**")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/payments/success")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/payments/cancel")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/payments/**")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/home/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/security/register")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/security/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                        .anyRequest().authenticated()
        )
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
                //.httpBasic(withDefaults());

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder();
    }

}
