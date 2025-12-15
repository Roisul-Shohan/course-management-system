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

import org.bson.Document;
import org.bson.types.ObjectId;

import com.cms.config.JWTconfig;
import com.cms.dao.CourseDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Teacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@WebServlet("/AdminCourseServlet")
public class AdminCourseServlet extends HttpServlet {

    private CourseDAO courseDAO = new CourseDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
            MongoCollection<Document> collection = courseDAO.courseCollection;
            MongoCursor<Document> cursor = collection.find().iterator();

            List<Map<String, Object>> courses = new ArrayList<>();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map<String, Object> course = new HashMap<>();
                course.put("id", doc.getObjectId("_id").toString());
                course.put("name", doc.getString("name"));
                course.put("courseCode", doc.getString("courseCode"));

                ObjectId assignedTeacherId = doc.getObjectId("assigned_teacher");
                String teacherName = null;
                if (assignedTeacherId != null) {
                    Teacher teacher = teacherDAO.findById(assignedTeacherId);
                    if (teacher != null) {
                        teacherName = teacher.getFullname();
                    }
                }
                course.put("assignedTeacher", teacherName);
                List<ObjectId> studentIds = doc.getList("students", ObjectId.class);

                course.put("students", studentIds != null ? studentIds.size() : 0);

                courses.add(course);
            }

            cursor.close();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("courses", courses);

            String json = objectMapper.writeValueAsString(responseData);
            out.print(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }
}