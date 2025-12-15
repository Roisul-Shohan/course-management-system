package com.cms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.cms.config.JWTconfig;
import com.cms.dao.CourseDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Course;

@WebServlet("/AssignTeacherServlet")
public class AssignTeacherServlet extends HttpServlet {

    private CourseDAO courseDAO = new CourseDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();

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
            String courseIdStr = request.getParameter("courseId");
            String teacherIdStr = request.getParameter("teacherId");

            if (courseIdStr == null || teacherIdStr == null || courseIdStr.trim().isEmpty()
                    || teacherIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Course ID and Teacher ID are required\"}");
                return;
            }

            ObjectId courseId = new ObjectId(courseIdStr.trim());
            ObjectId teacherId = new ObjectId(teacherIdStr.trim());

            Course course = courseDAO.findById(courseId);
            if (course != null) {
                ObjectId previousTeacherId = course.getAssignedTeacher();
                if (previousTeacherId != null && !previousTeacherId.equals(teacherId)) {
                    teacherDAO.removeCourseFromTeacher(previousTeacherId, courseId);
                }
            }

            courseDAO.assignTeacherToCourse(courseId, teacherId);
            teacherDAO.addCourseToTeacher(teacherId, courseId);

            out.print("{\"success\": true, \"message\": \"Teacher assigned to course successfully\"}");

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid ID format: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }
}