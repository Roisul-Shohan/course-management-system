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
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/StudentProfileServlet")
public class StudentProfileServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName()))
                    return cookie.getValue();
            }
        }
        return null;
    }

    private Student authenticateStudent(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            String token = getJwtFromCookies(request);
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"message\": \"Authentication required\"}");
                return null;
            }

            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String secret = dotenv.get("JWT_SECRET");
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);

            String username = decoded.getSubject();
            String role = decoded.getClaim("role").asString();

            if (!"student".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return null;
            }

            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Student not found\"}");
                return null;
            }

            return student;
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Student student = authenticateStudent(request, response, out);
        if (student == null)
            return;

        // Return JSON profile (without profileImage)
        String json = String.format(
                "{\"success\": true, \"profile\": {\"fullname\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}}",
                escapeJson(student.getFullname()),
                escapeJson(student.getUsername()),
                escapeJson(student.getEmail()));
        out.print(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Student student = authenticateStudent(request, response, out);
        if (student == null)
            return;

        try {
            // Get form data
            String fullname = request.getParameter("fullname");
            String newUsername = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Check username conflict
            if (!student.getUsername().equals(newUsername)) {
                if (studentDAO.findByUsername(newUsername) != null) {
                    out.print("{\"success\": false, \"message\": \"Username already taken\"}");
                    return;
                }
            }

            // Update student
            student.setFullname(fullname);
            student.setUsername(newUsername);
            student.setEmail(email);
            if (password != null && !password.isEmpty())
                student.setPassword(password);

            // Update MongoDB
            MongoDatabase database = DBconfig.getDatabase();
            MongoCollection<Document> studentCollection = database.getCollection("students");
            Document updateDoc = new Document()
                    .append("fullname", student.getFullname())
                    .append("username", student.getUsername())
                    .append("email", student.getEmail());

            if (password != null && !password.isEmpty())
                updateDoc.append("password", student.getPassword());

            studentCollection.updateOne(Filters.eq("_id", student.get_id()), new Document("$set", updateDoc));

            out.print("{\"success\": true, \"message\": \"Profile updated successfully\"}");

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
