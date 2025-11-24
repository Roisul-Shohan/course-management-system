package com.cms.servlet;

import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/AdminStatsServlet")
public class AdminStatsServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get total counts from database
            long totalStudents = studentDAO.studentCollection.countDocuments();

            long totalTeachers = teacherDAO.teacherCollection.countDocuments();

            long totalCourses = courseDAO.courseCollection.countDocuments();

            // Create response map
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", totalStudents);
            stats.put("totalTeachers", totalTeachers);
            stats.put("totalCourses", totalCourses);

            // Convert to JSON
            String json = objectMapper.writeValueAsString(stats);
            response.getWriter().write(json);

        } catch (Exception e) {
            System.err.println("AdminStatsServlet: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch stats\"}");
        }
    }
}