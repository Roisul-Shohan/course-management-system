package com.cms.servlet;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.dao.CourseDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Course;
import com.cms.model.Teacher;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/TeacherCoursesServlet")
public class TeacherCoursesServlet extends HttpServlet {

    private TeacherDAO teacherDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Validate environment and database connection first
            validateEnvironment();
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
            Dotenv dotenv = Dotenv.load();
            String secret = dotenv.get("JWT_SECRET");
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);

            String username = decoded.getSubject();
            String role = decoded.getClaim("role").asString();

            // Verify user is a teacher
            if (!"teacher".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return;
            }

            // Get teacher by username
            Teacher teacher = teacherDAO.findByUsername(username);
            if (teacher == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Teacher not found\"}");
                return;
            }

            // Get courses for this teacher
            List<ObjectId> courseIds = teacher.getCourses();
            List<Course> courses = new ArrayList<>();

            for (ObjectId courseId : courseIds) {
                Course course = courseDAO.findById(courseId);
                if (course != null) {
                    courses.add(course);
                }
            }

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"courses\": [");

            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                json.append("{");
                json.append("\"id\": \"").append(course.get_id().toString()).append("\",");
                json.append("\"name\": \"").append(escapeJson(course.getName())).append("\",");
                json.append("\"courseCode\": \"")
                        .append(escapeJson(course.getCourseCode() != null ? course.getCourseCode() : "N/A"))
                        .append("\",");
                json.append("\"studentCount\": ").append(course.getStudents().size());
                json.append("}");
                if (i < courses.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]}");
            response.getWriter().print(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
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

    private void validateEnvironment() throws Exception {
        // Check if .env file exists and has required variables
        try {
            Dotenv dotenv = Dotenv.load();
            String jwtSecret = dotenv.get("JWT_SECRET");
            if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
                throw new Exception("JWT_SECRET not found in environment variables");
            }
        } catch (Exception e) {
            throw new Exception("Environment configuration error: " + e.getMessage());
        }

        // Test database connection
        try {
            teacherDAO.findByUsername("test"); // Simple query to test connection
        } catch (Exception e) {
            throw new Exception("Database connection error: " + e.getMessage());
        }
    }
}