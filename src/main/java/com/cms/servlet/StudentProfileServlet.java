package com.cms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.dao.StudentDAO;
import com.cms.model.Student;

import io.github.cdimascio.dotenv.Dotenv;

@WebServlet("/StudentProfileServlet")
public class StudentProfileServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName()))
                    return cookie.getValue();
            }
        }
        return null;
    }

    private Student authenticateStudent(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            String token = getJwtFromCookies(request);
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"message\": \"Authentication required\"}");
                return null;
            }

            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String secret = dotenv.get("JWT_SECRET");
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);

            String username = decoded.getSubject();
            String role = decoded.getClaim("role").asString();

            if (!"student".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return null;
            }

            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Student not found\"}");
                return null;
            }

            return student;
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Student student = authenticateStudent(request, response, out);
        if (student == null)
            return;

        String json = String.format(
                "{\"success\": true, \"profile\": {\"fullname\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}}",
                escapeJson(student.getFullname()),
                escapeJson(student.getUsername()),
                escapeJson(student.getEmail()));
        out.print(json);
    }


    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
