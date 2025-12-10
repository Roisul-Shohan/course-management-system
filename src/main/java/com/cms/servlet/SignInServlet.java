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

                    // Set cookie (with SameSite and Secure when appropriate) and session, then redirect using context path
                    setJwtCookieAndSession(request, response, token, "student", student);
                    System.out.println("SignInServlet: Redirecting to student.jsp");
                    response.sendRedirect(request.getContextPath() + "/student.jsp");
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

                    setJwtCookieAndSession(request, response, token, "teacher", teacher);
                    System.out.println("SignInServlet: Redirecting to teacher.jsp");
                    response.sendRedirect(request.getContextPath() + "/teacher.jsp");
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

                setJwtCookieAndSession(request, response, token, "admin", null);
                System.out.println("SignInServlet: Redirecting to admin.jsp");
                response.sendRedirect(request.getContextPath() + "/admin.jsp");
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

    private void setJwtCookieAndSession(HttpServletRequest request, HttpServletResponse response, String token, String role, Object userObj) {
        int maxAge = 24 * 60 * 60; // 24 hours

        // Build Set-Cookie header manually to include SameSite and optional Secure flag
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append("jwt=").append(token).append("; Path=/; Max-Age=").append(maxAge).append("; HttpOnly; SameSite=None");

        boolean isSecure = request.isSecure();
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        if (!isSecure && forwardedProto != null && forwardedProto.equalsIgnoreCase("https")) {
            isSecure = true;
        }
        if (isSecure) {
            cookieBuilder.append("; Secure");
        }

        response.addHeader("Set-Cookie", cookieBuilder.toString());

        if (userObj != null) {
            HttpSession session = request.getSession();
            if ("student".equals(role)) session.setAttribute("student", userObj);
            else if ("teacher".equals(role)) session.setAttribute("teacher", userObj);
        }

        System.out.println("SignInServlet: JWT cookie set (SameSite=None) secure=" + isSecure);
    }
}
