package com.cms.servlet;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.config.DBconfig;
import com.cms.dao.StudentDAO;
import com.cms.model.Student;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/StudentActivityServlet")
public class StudentActivityServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
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

            // Get student
            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Student not found\"}");
                return;
            }

            // Get recent activities from database
            MongoDatabase database = DBconfig.getDatabase();
            MongoCollection<Document> activityCollection = database.getCollection("student_activities");

            // Get activities for this student, sorted by timestamp descending, limit to 10
            List<Document> activities = activityCollection.find(
                    Filters.eq("studentId", student.get_id()))
                    .sort(Sorts.descending("timestamp"))
                    .limit(10)
                    .into(new ArrayList<>());

            // If no activities exist, create some default ones
            if (activities.isEmpty()) {
                createDefaultActivities(student.get_id(), activityCollection);
                activities = activityCollection.find(
                        Filters.eq("studentId", student.get_id()))
                        .sort(Sorts.descending("timestamp"))
                        .limit(10)
                        .into(new ArrayList<>());
            }

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"activities\": [");

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm");

            for (int i = 0; i < activities.size(); i++) {
                Document activity = activities.get(i);
                String activityType = activity.getString("type");
                String description = activity.getString("description");
                Date timestamp = activity.getDate("timestamp");
                String timeAgo = getTimeAgo(timestamp);

                json.append("{");
                json.append("\"type\": \"").append(escapeJson(activityType)).append("\",");
                json.append("\"description\": \"").append(escapeJson(description)).append("\",");
                json.append("\"timeAgo\": \"").append(escapeJson(timeAgo)).append("\",");
                json.append("\"icon\": \"").append(getActivityIcon(activityType)).append("\",");
                json.append("\"color\": \"").append(getActivityColor(activityType)).append("\"");
                json.append("}");

                if (i < activities.size() - 1) {
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

    private void createDefaultActivities(ObjectId studentId, MongoCollection<Document> activityCollection) {
        // Create some default activities for demonstration
        List<Document> defaultActivities = new ArrayList<>();

        // Account created activity
        defaultActivities.add(new Document()
                .append("studentId", studentId)
                .append("type", "account")
                .append("description", "Account created successfully")
                .append("timestamp", new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000))); // 30 days ago

        // Profile update activity
        defaultActivities.add(new Document()
                .append("studentId", studentId)
                .append("type", "profile")
                .append("description", "Profile information updated")
                .append("timestamp", new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000))); // 7 days ago

        // Login activity
        defaultActivities.add(new Document()
                .append("studentId", studentId)
                .append("type", "login")
                .append("description", "Logged into the system")
                .append("timestamp", new Date(System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000))); // 2 days ago

        // Course enrollment activity
        defaultActivities.add(new Document()
                .append("studentId", studentId)
                .append("type", "enrollment")
                .append("description", "Enrolled in Introduction to Programming")
                .append("timestamp", new Date(System.currentTimeMillis() - 5L * 24 * 60 * 60 * 1000))); // 5 days ago

        // Insert all default activities
        activityCollection.insertMany(defaultActivities);
    }

    private String getTimeAgo(Date timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp.getTime();

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else {
            return "Just now";
        }
    }

    private String getActivityIcon(String type) {
        switch (type) {
            case "enrollment":
                return "C";
            case "profile":
                return "P";
            case "login":
                return "L";
            case "account":
                return "A";
            default:
                return "?";
        }
    }

    private String getActivityColor(String type) {
        switch (type) {
            case "enrollment":
                return "blue";
            case "profile":
                return "green";
            case "login":
                return "purple";
            case "account":
                return "orange";
            default:
                return "gray";
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