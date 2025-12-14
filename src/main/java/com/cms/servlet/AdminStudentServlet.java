package com.cms.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

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

            MongoCollection<Document> collection = studentDAO.studentCollection;
            MongoCursor<Document> cursor = collection.find().iterator();

            List<Map<String, Object>> students = new ArrayList<>();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map<String, Object> student = new HashMap<>();
                student.put("id", doc.getObjectId("_id").toString());
                student.put("username", doc.getString("username"));
                student.put("fullname", doc.getString("fullname"));
                student.put("email", doc.getString("email"));

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
                student.put("courses", courseNames);
                students.add(student);
            }

            cursor.close();

            String json = objectMapper.writeValueAsString(students);
            response.getWriter().write(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch students\"}");
        }
    }
}