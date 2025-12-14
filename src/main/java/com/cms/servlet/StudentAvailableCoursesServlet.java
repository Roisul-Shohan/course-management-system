package com.cms.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Course;
import com.cms.model.Student;
import com.cms.model.Teacher;

import io.github.cdimascio.dotenv.Dotenv;

@WebServlet("/StudentAvailableCoursesServlet")
public class StudentAvailableCoursesServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
        teacherDAO = new TeacherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
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

            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String secret = dotenv.get("JWT_SECRET");
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);

            String username = decoded.getSubject();
            String role = decoded.getClaim("role").asString();

            if (!"student".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return;
            }

            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Student not found\"}");
                return;
            }

            List<String> enrolledCourseIds = new ArrayList<>();
            for (Object courseId : student.getCourses()) {
                enrolledCourseIds.add(courseId.toString());
            }

            List<Course> allCourses = courseDAO.getAll();

            List<Course> availableCourses = new ArrayList<>();
            for (Course course : allCourses) {
                if (!enrolledCourseIds.contains(course.get_id().toString())) {
                    availableCourses.add(course);
                }
            }

            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < availableCourses.size(); i++) {
                Course course = availableCourses.get(i);
                String teacherName = "Not assigned";
                if (course.getAssignedTeacher() != null) {
                    Teacher teacher = teacherDAO.findById(course.getAssignedTeacher());
                    if (teacher != null) {
                        teacherName = teacher.getFullname();
                    }
                }
                json.append("{");
                json.append("\"id\": \"").append(course.get_id().toString()).append("\",");
                json.append("\"name\": \"").append(escapeJson(course.getName())).append("\",");
                json.append("\"courseCode\": \"")
                        .append(escapeJson(course.getCourseCode() != null ? course.getCourseCode() : "N/A"))
                        .append("\",");
                json.append("\"teacherName\": \"").append(escapeJson(teacherName)).append("\",");
                json.append("\"studentCount\": ").append(course.getStudents().size());
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