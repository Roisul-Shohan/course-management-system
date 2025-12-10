package com.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

        System.out.println("SignInServlet: Received signin request - username: " + username + ", role: " + role);

        if (role.equals("student")) {
            try {
                Student student = studentDAO.findByUsername(username);
                System.out.println("SignInServlet: Student found: " + (student != null ? student.getUsername() : "null"));
                if (student != null) {
                    boolean passwordValid = BCrypt.checkpw(password, student.getPassword());
                    System.out.println("SignInServlet: Password valid: " + passwordValid);

                    if (!passwordValid) {
                        // Forward with error attribute so the signin.jsp can show message immediately
                        request.setAttribute("error", "invalid_password");
                        request.getRequestDispatcher("signin.jsp").forward(request, response);
                        return;
                    }

                    String token = JWTconfig.generateToken(student.getUsername(), "student");
                    System.out.println("SignInServlet: JWT token generated for student");

                    Cookie jwtCookie = new Cookie("jwt", token);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setMaxAge(24 * 60 * 60);
                    jwtCookie.setPath("/");
                    response.addCookie(jwtCookie);

                    // Store student object in session
                    HttpSession session = request.getSession();
                    session.setAttribute("student", student);

                    System.out.println("SignInServlet: Redirecting to student.jsp");
                    response.sendRedirect("student.jsp");
                    return;
                }
            } catch (Exception e) {
                System.err.println("SignInServlet: Exception during student signin: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "user_not_found");
                request.getRequestDispatcher("signin.jsp").forward(request, response);
                return;
            }

        } else if (role.equals("teacher")) {
            try {
                Teacher teacher = teacherDAO.findByUsername(username);
                System.out.println("SignInServlet: Teacher found: " + (teacher != null ? teacher.getUsername() : "null"));
                if (teacher != null) {
                    boolean passwordValid = BCrypt.checkpw(password, teacher.getPassword());
                    System.out.println("SignInServlet: Password valid: " + passwordValid);
                    if (!passwordValid) {
                        request.setAttribute("error", "invalid_password");
                        request.getRequestDispatcher("signin.jsp").forward(request, response);
                        return;
                    }

                    String token = JWTconfig.generateToken(teacher.getUsername(), "teacher");
                    System.out.println("SignInServlet: JWT token generated for teacher");

                    Cookie jwtCookie = new Cookie("jwt", token);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setMaxAge(24 * 60 * 60);
                    jwtCookie.setPath("/");
                    response.addCookie(jwtCookie);

                    // Store teacher object in session
                    HttpSession session = request.getSession();
                    session.setAttribute("teacher", teacher);

                    System.out.println("SignInServlet: Redirecting to teacher.jsp");
                    response.sendRedirect("teacher.jsp");
                    return;
                }
            } catch (Exception e) {
                System.err.println("SignInServlet: Exception during teacher signin: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "user_not_found");
                request.getRequestDispatcher("signin.jsp").forward(request, response);
                return;
            }
        } else if (role.equals("admin")) {
            System.out.println("SignInServlet: Admin signin attempt");
            if (username.equals("admin") && password.equals("aaa")) {
                String token = JWTconfig.generateToken("admin", "admin");
                System.out.println("SignInServlet: JWT token generated for admin");

                Cookie jwtCookie = new Cookie("jwt", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setMaxAge(24 * 60 * 60);
                jwtCookie.setPath("/");
                response.addCookie(jwtCookie);

                System.out.println("SignInServlet: Redirecting to admin.jsp");
                response.sendRedirect("admin.jsp");
                return;

            } else {
                System.out.println("SignInServlet: Admin credentials invalid");
            }
        }

        // User not found — forward with attribute so page can display message
        System.out.println("SignInServlet: User not found or invalid role, forwarding to signin.jsp with error");
        request.setAttribute("error", "user_not_found");
        request.getRequestDispatcher("signin.jsp").forward(request, response);
    }
}
