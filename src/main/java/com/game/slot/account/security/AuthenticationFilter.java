package com.game.slot.account.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.slot.account.controller.model.LoginRequestModel;
import com.game.slot.account.service.UserService;
import com.game.slot.account.shared.dto.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${token.expiration}")
    private String expirationTimeSec;

    @Value("${token.secret}")
    private String tokenSecret;
    private final UserService userService;
    
    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService) {
        super(authenticationManager);
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
        String username = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(username);
        Instant now = Instant.now();
        String jwtToken = Jwts.builder()
                .subject(userDetails.getEmail())
                .expiration(Date.from(Instant.now().plusSeconds(Long.parseLong(expirationTimeSec))))
                .issuedAt(Date.from(now))
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
        response.addHeader("token", jwtToken);
        response.addHeader("userId", userDetails.getUserPublicId());
    }
}
