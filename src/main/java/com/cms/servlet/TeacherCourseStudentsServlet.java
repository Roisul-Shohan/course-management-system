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

import org.bson.types.ObjectId;

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

@WebServlet("/TeacherCourseStudentsServlet")
public class TeacherCourseStudentsServlet extends HttpServlet {

    private TeacherDAO teacherDAO;
    private CourseDAO courseDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        courseDAO = new CourseDAO();
        studentDAO = new StudentDAO();
    }

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

            if (!"teacher".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return;
            }

            Teacher teacher = teacherDAO.findByUsername(username);
            if (teacher == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Teacher not found\"}");
                return;
            }

            if (!teacher.getCourses().contains(courseId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied to this course\"}");
                return;
            }

            Course course = courseDAO.findById(courseId);
            if (course == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Course not found\"}");
                return;
            }

            List<ObjectId> studentIds = course.getStudents();
            System.out.println("Course: " + course.getName() + ", Student IDs: " + studentIds);
            List<Student> students = new ArrayList<>();

            for (ObjectId studentId : studentIds) {
                Student student = studentDAO.findById(studentId);
                if (student != null) {
                    students.add(student);
                    System.out.println("Found student: " + student.getFullname() + " (" + student.getUsername() + ")");
                } else {
                    System.out.println("Student not found for ID: " + studentId);
                }
            }
            System.out.println("Total students found: " + students.size());

            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"students\": [");

            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                json.append("{");
                json.append("\"id\": \"").append(student.get_id().toString()).append("\",");
                json.append("\"fullname\": \"").append(escapeJson(student.getFullname())).append("\",");
                json.append("\"username\": \"").append(escapeJson(student.getUsername())).append("\",");
                json.append("\"email\": \"").append(escapeJson(student.getEmail())).append("\"");
                json.append("}");
                if (i < students.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]}");
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