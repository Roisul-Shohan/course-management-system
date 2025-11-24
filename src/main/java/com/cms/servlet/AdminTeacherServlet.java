package com.cms.servlet;

import com.cms.dao.CourseDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Course;
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

@WebServlet("/AdminTeacherServlet")
public class AdminTeacherServlet extends HttpServlet {

    private TeacherDAO teacherDAO = new TeacherDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get all teachers from DAO
            MongoCollection<Document> collection = teacherDAO.teacherCollection;
            MongoCursor<Document> cursor = collection.find().iterator();

            List<Map<String, Object>> teachers = new ArrayList<>();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map<String, Object> teacher = new HashMap<>();
                teacher.put("id", doc.getObjectId("_id").toString());
                teacher.put("username", doc.getString("username"));
                teacher.put("fullname", doc.getString("fullname"));
                teacher.put("email", doc.getString("email"));

                // Get course names for this teacher
                List<String> courseNames = new ArrayList<>();
                List<ObjectId> courseIds = doc.getList("courses", ObjectId.class);
                if (courseIds != null) {
                    for (ObjectId courseId : courseIds) {
                        Course course = courseDAO.findById(courseId);
                        if (course != null) {
                            courseNames.add(course.getName() + " (" + course.getCourseCode() + ")");
                        }
                    }
                }
                teacher.put("courses", courseNames);

                teachers.add(teacher);
            }

            cursor.close();

            // Convert to JSON
            String json = objectMapper.writeValueAsString(teachers);
            response.getWriter().write(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch teachers\"}");
        }
    }
}