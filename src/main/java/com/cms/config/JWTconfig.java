package com.cms.config;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import io.github.cdimascio.dotenv.Dotenv;

public class JWTconfig {

    private static final Dotenv dotenv = Dotenv.load();

    public static final String SECRET;

    public static String getSecret() { return SECRET; }

    static {
        SECRET = dotenv.get("JWT_SECRET");
        if (SECRET == null || SECRET.isEmpty()) {
            throw new RuntimeException("JWT_SECRET environment variable is not set");
        }
    }
    private static final long EXPIRATION_TIME = Long.parseLong(dotenv.get("JWT_EXPIRATION_TIME", "86400000"));

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
            return jwt.getSubject(); 
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
            decodeToken(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
