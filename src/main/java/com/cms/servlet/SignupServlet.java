package com.cms.servlet;

import com.cms.config.JWTconfig;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Student;
import com.cms.model.Teacher;

import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        try {
            studentDAO = new StudentDAO();
            teacherDAO = new TeacherDAO();
        } catch (Exception e) {
            // Error initializing SignupServlet
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form parameters
        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmpassword = request.getParameter("confirmpassword");
        String role = request.getParameter("role"); // "student" or "teacher"

        // Process signup parameters

        // Check if DAOs are initialized
        if (studentDAO == null || teacherDAO == null) {
            System.err.println("Database connection failed. DAOs not initialized.");
            response.sendRedirect("signup.jsp?error=database");
            return;
        }

        if (role == null || (!role.equals("student") && !role.equals("teacher"))) {
            response.sendRedirect("signup.jsp?error=invalidrole");
            return;
        }

        // Check if username already exists
        if (role.equals("student") && studentDAO.findByUsername(username) != null) {
            response.sendRedirect("signup.jsp?error=usernameexists");
            return;
        } else if (role.equals("teacher") && teacherDAO.findByUsername(username) != null) {
            response.sendRedirect("signup.jsp?error=usernameexists");
            return;
        } else if (!password.equals(confirmpassword)) {
            response.sendRedirect("signup.jsp?error=password");
            return;
        }


        // Create user and save to DB
        try {
            long saveStartTime = System.currentTimeMillis();
            if (role.equals("student")) {
                Student student = new Student();
                student.setFullname(fullname);
                student.setUsername(username);
                student.setEmail(email);
                student.setPassword(password); // This will hash the password
                studentDAO.save(student);
            } else if (role.equals("teacher")) {
                Teacher teacher = new Teacher();
                teacher.setFullname(fullname);
                teacher.setUsername(username);
                teacher.setEmail(email);
                teacher.setPassword(password); // This will hash the password
                teacherDAO.save(teacher);
            }
            long saveEndTime = System.currentTimeMillis();
            System.out.println("SignupServlet: Save operation took " + (saveEndTime - saveStartTime) + " ms");

            String token = JWTconfig.generateToken(username, role);

            javax.servlet.http.Cookie jwtCookie = new javax.servlet.http.Cookie("jwt", token);
            jwtCookie.setHttpOnly(true); // cannot be accessed by JavaScript
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            if (role.equals("student")) {
                response.sendRedirect("student.jsp");
            } else {
                response.sendRedirect("teacher.jsp");
            }
        } catch (Exception e) {
            System.err.println("SignupServlet: Exception during save: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("signup.jsp?error=unknown");
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Just forward to signup.jsp if someone visits /signup via GET
        request.getRequestDispatcher("signup.jsp").forward(request, response);
    }
}