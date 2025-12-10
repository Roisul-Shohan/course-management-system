package com.cms.config;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import io.github.cdimascio.dotenv.Dotenv;

public class JWTconfig {

    // Load dotenv if present, but do not fail when .env is missing in production.
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String SECRET;

    static {
        String secretFromDotenv = null;
        try {
            if (dotenv != null) {
                secretFromDotenv = dotenv.get("JWT_SECRET");
            }
        } catch (Exception e) {
            // ignore - we'll try environment variables next
        }

        String secretFromEnv = System.getenv("JWT_SECRET");

        if (secretFromDotenv != null && !secretFromDotenv.isEmpty()) {
            SECRET = secretFromDotenv;
        } else if (secretFromEnv != null && !secretFromEnv.isEmpty()) {
            SECRET = secretFromEnv;
        } else {
            // Fallback secret (should be overridden in production). Log a warning.
            System.err.println("WARNING: JWT_SECRET not set. Using insecure default secret. Please set JWT_SECRET in environment or .env file.");
            SECRET = "default-change-me";
        }
    }
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
