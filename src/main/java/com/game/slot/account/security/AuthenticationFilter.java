package com.game.slot.account.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.slot.account.controller.model.LoginRequestModel;
import com.game.slot.account.service.UserService;
import com.game.slot.account.shared.dto.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
        String expirationTimeSec = environment.getProperty("token.expiration");
        String tokenSecret = environment.getProperty("token.secret");
        log.info("Successful authentication with expirationTimeSec: {} and tokenSecret: {}", expirationTimeSec , tokenSecret);
        String username = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(username);
        Instant now = Instant.now();
        String jwtToken = Jwts.builder()
                .setSubject(userDetails.getEmail())
                .setExpiration(Date.from(now.plusSeconds(Long.parseLong(expirationTimeSec))))
                .setIssuedAt(Date.from(now))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret)), SignatureAlgorithm.HS256)
                .compact();
        
        
        response.addHeader("token", jwtToken);
        response.addHeader("userId", userDetails.getUserPublicId());
    }
}
