package com.cms.servlet;

import com.cms.dao.CourseDAO;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Teacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/AdminActivityServlet")
public class AdminActivityServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Map<String, Object>> activities = new ArrayList<>();

            // Get recent students (last 10)
            MongoCollection<Document> studentCollection = studentDAO.studentCollection;
            FindIterable<Document> recentStudents = studentCollection.find()
                    .sort(new Document("_id", -1)).limit(5);

            for (Document doc : recentStudents) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("type", "student");
                activity.put("description", "New student registered");
                activity.put("name", doc.getString("fullname"));
                activity.put("timestamp", formatTimestamp(doc.getObjectId("_id").getTimestamp() * 1000L));
                activities.add(activity);
            }

            // Get recent courses (last 5)
            MongoCollection<Document> courseCollection = courseDAO.courseCollection;
            FindIterable<Document> recentCourses = courseCollection.find()
                    .sort(new Document("_id", -1)).limit(3);

            for (Document doc : recentCourses) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("type", "course");
                activity.put("description", "New course created");
                activity.put("name", doc.getString("name"));
                activity.put("timestamp", formatTimestamp(doc.getObjectId("_id").getTimestamp() * 1000L));
                activities.add(activity);
            }

            // Get recent teacher assignments (courses with assigned teachers)
            FindIterable<Document> assignedCourses = courseCollection.find(
                    new Document("assigned_teacher", new Document("$exists", true)))
                    .sort(new Document("_id", -1)).limit(2);

            for (Document doc : assignedCourses) {
                ObjectId teacherId = doc.getObjectId("assigned_teacher");
                Teacher teacher = teacherDAO.findById(teacherId);
                if (teacher != null) {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("type", "assignment");
                    activity.put("description", "Teacher assigned to course");
                    activity.put("name", teacher.getFullname());
                    activity.put("course", doc.getString("name"));
                    activity.put("timestamp", formatTimestamp(doc.getObjectId("_id").getTimestamp() * 1000L));
                    activities.add(activity);
                }
            }

            // Sort activities by timestamp (most recent first)
            activities.sort((a, b) -> ((String) b.get("timestamp")).compareTo((String) a.get("timestamp")));

            // Return only the most recent 5 activities
            List<Map<String, Object>> recentActivities = activities.subList(0, Math.min(5, activities.size()));

            String json = objectMapper.writeValueAsString(recentActivities);
            response.getWriter().write(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch activities\"}");
        }
    }

    private String formatTimestamp(long timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd 'at' hh:mm a");
            Date date = new Date(timestamp);
            long diff = System.currentTimeMillis() - timestamp;
            long days = diff / (24 * 60 * 60 * 1000);

            if (days == 0) {
                return "Today " + sdf.format(date).split(" at ")[1];
            } else if (days == 1) {
                return "Yesterday";
            } else if (days < 7) {
                return days + " days ago";
            } else {
                return sdf.format(date);
            }
        } catch (Exception e) {
            return "Recently";
        }
    }
}