package com.cms.servlet;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.model.Course;
import com.cms.model.Student;
import io.github.cdimascio.dotenv.Dotenv;

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

@WebServlet("/StudentAvailableCoursesServlet")
public class StudentAvailableCoursesServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
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

            // Verify user is a student
            if (!"student".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return;
            }

            // Get student by username
            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Student not found\"}");
                return;
            }

            // Get enrolled course IDs
            List<String> enrolledCourseIds = new ArrayList<>();
            for (Object courseId : student.getCourses()) {
                enrolledCourseIds.add(courseId.toString());
            }

            // Get all courses
            List<Course> allCourses = courseDAO.getAll();

            // Filter out enrolled courses
            List<Course> availableCourses = new ArrayList<>();
            for (Course course : allCourses) {
                if (!enrolledCourseIds.contains(course.get_id().toString())) {
                    availableCourses.add(course);
                }
            }

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < availableCourses.size(); i++) {
                Course course = availableCourses.get(i);
                json.append("{");
                json.append("\"id\": \"").append(course.get_id().toString()).append("\",");
                json.append("\"name\": \"").append(escapeJson(course.getName())).append("\",");
                json.append("\"courseCode\": \"")
                        .append(escapeJson(course.getCourseCode() != null ? course.getCourseCode() : "N/A"))
                        .append("\"");

                json.append("}");
                if (i < availableCourses.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");
            out.print(json.toString());

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
}