package com.game.slot.account.security;

import com.game.slot.account.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Environment environment;
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(Environment environment, @Lazy UserService userService, @Lazy PasswordEncoder passwordEncoder) {
        this.environment = environment;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String allowedIp = environment.getProperty("gateway.ip");
        log.info("Security filter chain with allowed ip: {}", allowedIp);
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(HttpMethod.GET, "/users/status").permitAll().anyRequest().authenticated()
                        .requestMatchers(HttpMethod.POST, "/**")
                        .access(new WebExpressionAuthorizationManager("hasIpAddress('" + allowedIp + "')")))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/users/status").permitAll().anyRequest().authenticated())
                .addFilter(new AuthenticationFilter(authenticationManager, environment, userService))
                .authenticationManager(authenticationManager)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
