package com.cms.servlet;

import com.cms.config.JWTconfig;
import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.model.Course;
import com.cms.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.bson.types.ObjectId;

@WebServlet("/StudentRegisterCourseServlet")
public class StudentRegisterCourseServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Authenticate user via JWT
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
            if (token == null || !JWTconfig.isTokenValid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or missing token\"}");
                return;
            }

            String role = JWTconfig.getRoleFromToken(token);
            if (!"student".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"Access denied\"}");
                return;
            }

            String username = JWTconfig.getUsernameFromToken(token);
            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Student not found\"}");
                return;
            }

            // Validate course ID from request
            String courseIdStr = request.getParameter("courseId");
            if (courseIdStr == null || courseIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Course ID is required\"}");
                return;
            }

            ObjectId courseId;
            try {
                courseId = new ObjectId(courseIdStr);
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid course ID\"}");
                return;
            }

            Course course = courseDAO.findById(courseId);
            if (course == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Course not found\"}");
                return;
            }

            // Check if student is not already registered
            if (student.getCourses().contains(courseId)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\": \"Already registered for this course\"}");
                return;
            }

            // Add course ID to student's courses list
            studentDAO.addCourseToStudent(student.get_id(), courseId);

            // Add student ID to course's students list
            courseDAO.addStudentToCourse(course.get_id(), student.get_id());

            // Return success message
            response.getWriter().write("{\"success\": true, \"message\": \"Successfully registered for the course\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error: " + e.getMessage() + "\"}");
        }
    }
}