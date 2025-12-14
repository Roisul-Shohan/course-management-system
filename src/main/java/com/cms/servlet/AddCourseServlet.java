package com.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        try {
            String courseName = request.getParameter("courseName");
            String courseCode = request.getParameter("courseCode");

            if (courseName == null || courseName.trim().isEmpty() ||
                    courseCode == null || courseCode.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Course name and code are required\"}");
                return;
            }

            // Check if course code already exists
            if (courseDAO.findByCourseCode(courseCode.trim()) != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Course code already exists\"}");
                return;
            }

            Course course = new Course(courseName.trim(), courseCode.trim());
            courseDAO.save(course);

            response.getWriter().write("{\"success\": true, \"message\": \"Course added successfully\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to add course: " + e.getMessage() + "\"}");
        }
    }

}