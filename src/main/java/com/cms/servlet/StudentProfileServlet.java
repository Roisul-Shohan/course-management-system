package com.cms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cms.config.JWTconfig;
import com.cms.dao.StudentDAO;
import com.cms.model.Student;

@WebServlet("/StudentProfileServlet")
public class StudentProfileServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Unauthorized\"}");
            return;
        }
        if (!JWTconfig.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Invalid token\"}");
            return;
        }
        String role = JWTconfig.getRoleFromToken(token);
        if (!"student".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\": false, \"message\": \"Access denied\"}");
            return;
        }

        String username = JWTconfig.getUsernameFromToken(token);
        Student student = studentDAO.findByUsername(username);
        if (student == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"success\": false, \"message\": \"Student not found\"}");
            return;
        }

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
