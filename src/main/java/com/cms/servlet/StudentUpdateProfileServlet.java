package com.cms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cms.config.DBconfig;
import com.cms.config.JWTconfig;
import com.cms.dao.StudentDAO;
import com.cms.model.Student;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import io.github.cdimascio.dotenv.Dotenv;

@WebServlet("/StudentUpdateProfileServlet")
public class StudentUpdateProfileServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
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

            if (!"student".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Access denied\"}");
                return;
            }

            String fullname = request.getParameter("fullname");
            String newUsername = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password"); 

            if (fullname == null || fullname.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Full name is required\"}");
                return;
            }
            if (newUsername == null || newUsername.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Username is required\"}");
                return;
            }
            if (email == null || email.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Email is required\"}");
                return;
            }

            Student student = studentDAO.findByUsername(username);
            if (student == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Student not found\"}");
                return;
            }


            if (!username.equals(newUsername)) { 
                Student existing = studentDAO.findByUsername(newUsername);
                if (existing != null) {
                    out.print("{\"success\": false, \"message\": \"Username already taken\"}");
                    return;
                }
                student.setUsername(newUsername); 
            }

            student.setFullname(fullname);
            student.setEmail(email);
            if (password != null && !password.trim().isEmpty()) {
                student.setPassword(password); 
            }

            MongoDatabase database = DBconfig.getDatabase();
            MongoCollection<Document> studentCollection = database.getCollection("students");

            Document updateDoc = new Document()
                    .append("fullname", student.getFullname())
                    .append("username", student.getUsername())
                    .append("email", student.getEmail());
            if (password != null && !password.trim().isEmpty()) {
                updateDoc.append("password", student.getPassword());
            }

            studentCollection.updateOne(Filters.eq("_id", student.get_id()), new Document("$set", updateDoc));

            if (!username.equals(newUsername)) {
                String newToken = JWTconfig.generateToken(newUsername, "student");
                Cookie jwtCookie = new Cookie("jwt", newToken);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setMaxAge(24 * 60 * 60);
                jwtCookie.setPath("/");
                response.addCookie(jwtCookie);
            }

            out.print("{\"success\": true, \"message\": \"Profile updated successfully\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }
}
