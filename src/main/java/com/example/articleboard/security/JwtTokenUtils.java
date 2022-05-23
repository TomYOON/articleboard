package com.example.articleboard.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    final Algorithm algorithm = Algorithm.HMAC256("secret".getBytes()); //TODO: secret은 따로 처리하여 보관해야됨
    final String issuer = "articleboard.example.com";
    final int ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000;
    final int REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private String createToken(Authentication authentication, int expirationTime) {
        User user = (User) authentication.getPrincipal();

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withIssuer(issuer)
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    public String getUsernameFromToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();

        return username;
    }
}
