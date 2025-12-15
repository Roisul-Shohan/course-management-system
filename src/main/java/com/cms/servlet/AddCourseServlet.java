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
import com.cms.dao.CourseDAO;
import com.cms.model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/AddCourseServlet")
public class AddCourseServlet extends HttpServlet {

    private CourseDAO courseDAO = new CourseDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
        if (!"admin".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\": false, \"message\": \"Access denied\"}");
            return;
        }

        try {
            String courseName = request.getParameter("courseName");
            String courseCode = request.getParameter("courseCode");

            if (courseName == null || courseName.trim().isEmpty() ||
                    courseCode == null || courseCode.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Course name and code are required\"}");
                return;
            }

            if (courseDAO.findByCourseCode(courseCode.trim()) != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Course code already exists\"}");
                return;
            }

            Course course = new Course(courseName.trim(), courseCode.trim());
            courseDAO.save(course);

            out.print("{\"success\": true, \"message\": \"Course added successfully\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }

}