package com.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.cms.dao.CourseDAO;
import com.cms.dao.TeacherDAO;

@WebServlet("/AssignTeacherServlet")
@MultipartConfig
public class AssignTeacherServlet extends HttpServlet {

    private CourseDAO courseDAO = new CourseDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get form parameters from multipart data
            String courseIdStr = request.getParameter("courseId");
            String teacherIdStr = request.getParameter("teacherId");

            // Validate input
            if (courseIdStr == null || teacherIdStr == null || courseIdStr.trim().isEmpty()
                    || teacherIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter()
                        .write("{\"success\": false, \"error\": \"Course ID and Teacher ID are required\"}");
                return;
            }

            // Convert strings to ObjectId
            ObjectId courseId = new ObjectId(courseIdStr.trim());
            ObjectId teacherId = new ObjectId(teacherIdStr.trim());

            // Assign teacher to course
            courseDAO.assignTeacherToCourse(courseId, teacherId);
            teacherDAO.addCourseToTeacher(teacherId, courseId);

            response.getWriter().write("{\"success\": true, \"message\": \"Teacher assigned to course successfully\"}");

        } catch (IllegalArgumentException e) {
            // Handle invalid ObjectId format
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter()
                    .write("{\"success\": false, \"error\": \"Invalid ID format: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter()
                    .write("{\"success\": false, \"error\": \"Failed to assign teacher: " + e.getMessage() + "\"}");
        }
    }
}