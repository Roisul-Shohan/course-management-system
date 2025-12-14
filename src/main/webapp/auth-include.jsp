<%@ page import="com.auth0.jwt.JWT" %>
<%@ page import="com.auth0.jwt.algorithms.Algorithm" %>
<%@ page import="com.auth0.jwt.interfaces.DecodedJWT" %>
<%@ page import="com.cms.config.JWTconfig" %>

<%
    Cookie[] cookies = request.getCookies();
    String token = null;

    if (cookies != null) {
        for (Cookie c : cookies) {
            if ("jwt".equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }
    }

    if (token == null) {
        response.sendRedirect("signin.jsp");
        return;
    }

    String username = "";
    String role = "";

    try {
        String SECRET = JWTconfig.getSecret();
        if (SECRET == null || SECRET.isEmpty()) {
            throw new RuntimeException("JWT_SECRET environment variable is not set");
        }

        DecodedJWT decoded = JWT
            .require(Algorithm.HMAC256(SECRET))
            .build()
            .verify(token);

        username = decoded.getSubject();
        role = decoded.getClaim("role").asString();

    } catch (Exception e) {
        response.sendRedirect("signin.jsp");
        return;
    }
%>