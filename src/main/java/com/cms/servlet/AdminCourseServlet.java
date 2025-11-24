package com.cms.servlet;

import com.cms.dao.CourseDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Teacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        try {
            // Get all courses from DAO
            MongoCollection<Document> collection = courseDAO.courseCollection;
            MongoCursor<Document> cursor = collection.find().iterator();

            List<Map<String, Object>> courses = new ArrayList<>();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map<String, Object> course = new HashMap<>();
                course.put("id", doc.getObjectId("_id").toString());
                course.put("name", doc.getString("name"));
                course.put("courseCode", doc.getString("courseCode"));

                // Get teacher name if assigned
                ObjectId assignedTeacherId = doc.getObjectId("assigned_teacher");
                String teacherName = null; // Use null instead of "Not Assigned"
                course.put("assignedTeacher", teacherName);
                List<ObjectId> studentIds = doc.getList("students", ObjectId.class);

                course.put("students", studentIds != null ? studentIds.size() : 0);

                courses.add(course);
            }

            cursor.close();

            // Convert to JSON
            String json = objectMapper.writeValueAsString(courses);
            response.getWriter().write(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch courses\"}");
        }
    }
}