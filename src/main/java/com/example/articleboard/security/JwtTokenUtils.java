package com.example.articleboard.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Getter
public class JwtTokenUtils {

    final Algorithm algorithm = Algorithm.HMAC256("secret".getBytes()); //TODO: secret은 따로 처리하여 보관해야됨
    final String issuer = "articleboard.example.com";
    final int ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000;
    final int REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";
    final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN";

    String token;
    JWTVerifier verifier;
    DecodedJWT decodedJWT;

    public void setToken(String token) {
        this.token = token;
        verifier = JWT.require(algorithm).build();
        decodedJWT = verifier.verify(token);
    }

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

    public String getUsername() {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();

        return username;
    }

    public Collection<SimpleGrantedAuthority> getAuthorities() {
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public void setTokenInCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_KEY, accessToken);
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_KEY, refreshToken);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    public String getTokenInCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(ACCESS_TOKEN_KEY))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
