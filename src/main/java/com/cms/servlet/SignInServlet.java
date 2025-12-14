package com.cms.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cms.config.JWTconfig;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Student;
import com.cms.model.Teacher;

@WebServlet("/signin")
public class SignInServlet extends HttpServlet {

    private String SECRET;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        try {
            studentDAO = new StudentDAO();
            teacherDAO = new TeacherDAO();
            SECRET = JWTconfig.getSecret();
            if (SECRET == null || SECRET.isEmpty()) {
                throw new RuntimeException("JWT_SECRET environment variable is not set");
            }
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
            try {
                Student student = studentDAO.findByUsername(username);
               
                if (student != null) {

                    boolean passwordValid = BCrypt.checkpw(password, student.getPassword());
                    if (!passwordValid) {
                        request.setAttribute("error", "invalid_password");
                        request.getRequestDispatcher("signin.jsp").forward(request, response);
                        return;
                    }

                    Algorithm algorithm = Algorithm.HMAC256(SECRET);
                    String token = JWT.create()
                        .withSubject(student.getUsername())
                        .withClaim("role", "student")
                        .withIssuedAt(new Date())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                        .sign(algorithm);
                   
                    javax.servlet.http.Cookie jwtCookie = new javax.servlet.http.Cookie("jwt", token);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setMaxAge(24 * 60 * 60);
                    jwtCookie.setPath("/");
                    response.addCookie(jwtCookie);

                    HttpSession session = request.getSession();
                    session.setAttribute("student", student);

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
               
                if (teacher != null) {

                    boolean passwordValid = BCrypt.checkpw(password, teacher.getPassword());
                    if (!passwordValid) {
                        request.setAttribute("error", "invalid_password");
                        request.getRequestDispatcher("signin.jsp").forward(request, response);
                        return;
                    }

                    Algorithm algorithm = Algorithm.HMAC256(SECRET);
                    String token = JWT.create()
                        .withSubject(teacher.getUsername())
                        .withClaim("role", "teacher")
                        .withIssuedAt(new Date())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                        .sign(algorithm);
                   

                    javax.servlet.http.Cookie jwtCookie = new javax.servlet.http.Cookie("jwt", token);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setMaxAge(24 * 60 * 60);
                    jwtCookie.setPath("/");
                    response.addCookie(jwtCookie);

                    HttpSession session = request.getSession();
                    session.setAttribute("teacher", teacher);

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
           
            if (username.equals("admin") && password.equals("aaa")) {
                Algorithm algorithm = Algorithm.HMAC256(SECRET);
                String token = JWT.create()
                    .withSubject("admin")
                    .withClaim("role", "admin")
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                    .sign(algorithm);
                
                javax.servlet.http.Cookie jwtCookie = new javax.servlet.http.Cookie("jwt", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setMaxAge(24 * 60 * 60);
                jwtCookie.setPath("/");
                response.addCookie(jwtCookie);

                response.sendRedirect(request.getContextPath() + "/admin.jsp");
                return;

            } else {
                System.out.println("SignInServlet: Admin credentials invalid");
            }
        }

       
        request.setAttribute("error", "user_not_found");
        request.getRequestDispatcher("signin.jsp").forward(request, response);
    }

}
