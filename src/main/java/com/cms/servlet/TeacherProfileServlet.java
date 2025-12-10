package com.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.dao.TeacherDAO;
import com.cms.model.Teacher;

import io.github.cdimascio.dotenv.Dotenv;

@WebServlet("/TeacherProfileServlet")
public class TeacherProfileServlet extends HttpServlet {

    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Teacher authenticateTeacher(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String token = getJwtFromCookies(request);
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print("{\"success\": false, \"message\": \"Authentication required\"}");
                return null;
            }

            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String secret = dotenv.get("JWT_SECRET");
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);

            String username = decoded.getSubject();
            String role = decoded.getClaim("role").asString();

            if (!"teacher".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().print("{\"success\": false, \"message\": \"Access denied\"}");
                return null;
            }

            Teacher teacher = teacherDAO.findByUsername(username);
            if (teacher == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"success\": false, \"message\": \"Teacher not found\"}");
                return null;
            }

            return teacher;

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"success\": false, \"message\": \"Internal server error\"}");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Teacher teacher = authenticateTeacher(request, response);
        if (teacher == null)
            return;

        // Return JSON profile (without profileImage)
        String json = String.format(
                "{\"success\": true, \"profile\": {\"fullname\": \"%s\", \"username\": \"%s\", \"email\": \"%s\"}}",
                escapeJson(teacher.getFullname()),
                escapeJson(teacher.getUsername()),
                escapeJson(teacher.getEmail()));

        response.getWriter().print(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Teacher teacher = authenticateTeacher(request, response);
        if (teacher == null)
            return;

        try {
            // Get form parameters
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");
            String newUsername = request.getParameter("username");

           

            // Check username conflict
            if (!teacher.getUsername().equals(newUsername)) {
                if (teacherDAO.findByUsername(newUsername) != null) {
                    response.getWriter().print("{\"success\": false, \"message\": \"Username already taken\"}");
                    return;
                }
            }

            // Debug logging for teacher object before update

            // Update teacher object
            teacher.setFullname(fullname);
            teacher.setEmail(email);
            teacher.setUsername(newUsername);

          
            // Update in database
            teacherDAO.updateTeacher(teacher);

            response.getWriter().print("{\"success\": true, \"message\": \"Profile updated successfully\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"success\": false, \"message\": \"Internal server error\"}");
        }
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
