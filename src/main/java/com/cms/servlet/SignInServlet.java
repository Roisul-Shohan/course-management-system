package com.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.cms.config.JWTconfig;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Student;
import com.cms.model.Teacher;

@WebServlet("/signin")
public class SignInServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        try {
            studentDAO = new StudentDAO();
            teacherDAO = new TeacherDAO();
        } catch (Exception e) {
            System.err.println("Error initializing SignupServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (role.equals("student")) {

            Student student = studentDAO.findByUsername(username);
            if (student != null) {
                boolean passwordValid = BCrypt.checkpw(password, student.getPassword());

                if (!passwordValid) {
                    // Forward with error attribute so the signin.jsp can show message immediately
                    request.setAttribute("error", "invalid_password");
                    request.getRequestDispatcher("signin.jsp").forward(request, response);
                    return;
                }

                String token = JWTconfig.generateToken(student.getUsername(), "student");

                Cookie jwtCookie = new Cookie("jwt", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setMaxAge(24 * 60 * 60);
                jwtCookie.setPath("/");
                response.addCookie(jwtCookie);

                // Store student object in session
                HttpSession session = request.getSession();
                session.setAttribute("student", student);

                response.sendRedirect("student.jsp");
                return;
            }

        } else if (role.equals("teacher")) {

            Teacher teacher = teacherDAO.findByUsername(username);
            if (teacher != null) {
                if (!BCrypt.checkpw(password, teacher.getPassword())) {
                    request.setAttribute("error", "invalid_password");
                    request.getRequestDispatcher("signin.jsp").forward(request, response);
                    return;
                }

                String token = JWTconfig.generateToken(teacher.getUsername(), "teacher");

                Cookie jwtCookie = new Cookie("jwt", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setMaxAge(24 * 60 * 60);
                jwtCookie.setPath("/");
                response.addCookie(jwtCookie);

                // Store teacher object in session
                HttpSession session = request.getSession();
                session.setAttribute("teacher", teacher);

                response.sendRedirect("teacher.jsp");
                return;
            }
        } else if (role.equals("admin")) {

            if (username.equals("admin") && password.equals("aaa")) {
                String token = JWTconfig.generateToken("admin", "admin");

                Cookie jwtCookie = new Cookie("jwt", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setMaxAge(24 * 60 * 60);
                jwtCookie.setPath("/");
                response.addCookie(jwtCookie);

                response.sendRedirect("admin.jsp");
                return;

            }
        }

        // User not found — forward with attribute so page can display message
        request.setAttribute("error", "user_not_found");
        request.getRequestDispatcher("signin.jsp").forward(request, response);
    }
}
