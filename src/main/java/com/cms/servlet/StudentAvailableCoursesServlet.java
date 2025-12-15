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

import com.cms.config.JWTconfig;
import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Course;
import com.cms.model.Student;
import com.cms.model.Teacher;

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
                out.print("{\"message\": \"Unauthorized\"}");
                return;
            }
            if (!JWTconfig.isTokenValid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"message\": \"Invalid token\"}");
                return;
            }
            String role = JWTconfig.getRoleFromToken(token);
            if (!"student".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"message\": \"Access denied\"}");
                return;
            }

            String username = JWTconfig.getUsernameFromToken(token);
            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Student not found\"}");
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
            out.print("{\"message\": \"Internal server error\"}");
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