package com.game.slot.account.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.slot.account.controller.model.LoginRequestModel;
import com.game.slot.account.service.UserService;
import com.game.slot.account.shared.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Environment environment;

    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, Environment environment, UserService userService) {
        super(authenticationManager);
        this.environment = environment;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestModel loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword(),
                    new ArrayList<>()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String expirationTime = environment.getProperty("token.expiration");
        String tokenSecret = environment.getProperty("token.secret");
        log.info("Successful authentication with expirationTimeSec: {} and tokenSecret: {}", expirationTime, tokenSecret);
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(username);
        Instant now = Instant.now();
        String jwtToken = JWT.create()
                .withSubject(userDetails.getEmail())
                .withExpiresAt(Date.from(now.plusSeconds(Long.parseLong(expirationTime))))
                .sign(Algorithm.HMAC256(tokenSecret));

        response.addHeader("token", jwtToken);
        response.addHeader("userId", userDetails.getUserPublicId());
    }
}
