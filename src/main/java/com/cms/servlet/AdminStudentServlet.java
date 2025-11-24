package com.cms.servlet;

import com.cms.dao.StudentDAO;
import com.cms.dao.CourseDAO;
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

@WebServlet("/AdminStudentServlet")
public class AdminStudentServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get all students from DAO
            MongoCollection<Document> collection = studentDAO.studentCollection;
            MongoCursor<Document> cursor = collection.find().iterator();

            List<Map<String, Object>> students = new ArrayList<>();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map<String, Object> student = new HashMap<>();
                student.put("username", doc.getString("username"));
                student.put("fullname", doc.getString("fullname"));
                student.put("email", doc.getString("email"));
                // Get course names instead of IDs
                List<?> coursesList = doc.getList("courses", Object.class);
                List<String> courseNames = new ArrayList<>();
                if (coursesList != null) {
                    for (Object courseId : coursesList) {
                        ObjectId courseObjectId = null;
                        if (courseId instanceof String) {
                            try {
                                courseObjectId = new ObjectId((String) courseId);
                            } catch (IllegalArgumentException e) {
                                // Invalid ObjectId string, skip
                                continue;
                            }
                        } else if (courseId instanceof org.bson.types.ObjectId) {
                            courseObjectId = (org.bson.types.ObjectId) courseId;
                        }
                        if (courseObjectId != null) {
                            Course course = courseDAO.findById(courseObjectId);
                            if (course != null) {
                                courseNames.add(course.getName());
                            } else {
                                // If course not found, add the ID as fallback
                                courseNames.add(courseObjectId.toString());
                            }
                        }
                    }
                }
                student.put("courses", courseNames);
                students.add(student);
            }

            cursor.close();

            // Convert to JSON
            String json = objectMapper.writeValueAsString(students);
            response.getWriter().write(json);

        } catch (Exception e) {
            System.err.println("AdminStudentServlet: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch students\"}");
        }
    }
}