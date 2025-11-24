package com.cms.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Date;

public class JWTconfig {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SECRET = dotenv.get("JWT_SECRET");
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours

    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public static String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    private static DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public static String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = decodeToken(token);
            return jwt.getSubject(); // subject = username
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRoleFromToken(String token) {
        try {
            DecodedJWT jwt = decodeToken(token);
            return jwt.getClaim("role").asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isTokenValid(String token) {
        try {
            decodeToken(token); // If token invalid → throws exception
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
