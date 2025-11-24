package com.cms.servlet;

import com.cms.dao.CourseDAO;
import com.cms.model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@WebServlet("/AddCourseServlet")
@MultipartConfig
public class AddCourseServlet extends HttpServlet {

    private CourseDAO courseDAO = new CourseDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get form parameters from multipart data
            Part courseNamePart = request.getPart("courseName");
            Part courseCodePart = request.getPart("courseCode");

            String courseName = getValue(courseNamePart);
            String courseCode = getValue(courseCodePart);


            // Validate input
            if (courseName == null || courseName.trim().isEmpty() ||
                    courseCode == null || courseCode.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Course name and code are required\"}");
                return;
            }

            // Create new course
            Course course = new Course(courseName.trim(), courseCode.trim());

            // Save to database
            courseDAO.save(course);

            // Return success response
            response.getWriter().write("{\"success\": true, \"message\": \"Course added successfully\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to add course: " + e.getMessage() + "\"}");
        }
    }

    private String getValue(Part part) throws IOException {
        if (part == null) {
            return null;
        }
        try (InputStream inputStream = part.getInputStream();
                Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            return scanner.useDelimiter("\\A").next();
        }
    }
}