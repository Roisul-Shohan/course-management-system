package com.cms.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.model.Course;
import com.cms.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

@WebServlet("/AdminCourseStudentsServlet")
public class AdminCourseStudentsServlet extends HttpServlet {

    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private com.cms.dao.TeacherDAO teacherDAO = new com.cms.dao.TeacherDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Course ID is required\"}");
            return;
        }

        try {
            ObjectId courseId = new ObjectId(courseIdParam);

            // Get JWT token from cookies
            String token = null;
            Cookie[] cookies = request.getCookies();
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
                out.print("{\"success\": false, \"message\": \"No authentication token found\"}");
                return;
            }

            // Validate JWT token
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String secret = dotenv.get("JWT_SECRET");
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);

            String username = decoded.getSubject();
            String role = decoded.getClaim("role").asString();

            // Verify user is an admin
            if (!"admin".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return;
            }

            // Get course
            Course course = courseDAO.findById(courseId);
            if (course == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Course not found\"}");
                return;
            }

            // Get students for this course
            List<ObjectId> studentIds = course.getStudents();
            List<Map<String, Object>> students = new ArrayList<>();

            for (ObjectId studentId : studentIds) {
                Student student = studentDAO.findById(studentId);
                if (student != null) {
                    Map<String, Object> studentData = new HashMap<>();
                    studentData.put("id", student.get_id().toString());
                    studentData.put("fullname", student.getFullname());
                    studentData.put("username", student.getUsername());
                    studentData.put("email", student.getEmail());
                    students.add(studentData);
                }
            }

            // Get teacher name for the course
            String teacherName = "Not Assigned";
            if (course.getAssignedTeacher() != null) {
                com.cms.model.Teacher teacher = teacherDAO.findById(course.getAssignedTeacher());
                if (teacher != null) {
                    teacherName = teacher.getFullname();
                }
            }

            // Build JSON response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("students", students);
            responseData.put("teacher", teacherName);

            String json = objectMapper.writeValueAsString(responseData);
            out.print(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }
}