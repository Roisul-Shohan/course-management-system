package com.cms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cms.config.JWTconfig;
import com.cms.dao.StudentDAO;
import com.cms.dao.TeacherDAO;
import com.cms.model.Student;
import com.cms.model.Teacher;

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
            System.out.println("shs");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmpassword = request.getParameter("confirmpassword");
        String role = request.getParameter("role"); 

        String isAjax = request.getHeader("X-Requested-With");
        boolean ajax = "XMLHttpRequest".equals(isAjax);

        if (studentDAO == null || teacherDAO == null) {
            System.err.println("Database connection failed. DAOs not initialized.");
            if (ajax) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"error\": \"Database connection failed\"}");
                out.flush();
                return;
            } else {
                response.sendRedirect("signup.jsp?error=database");
                return;
            }
        }

        if (role == null || (!role.equals("student") && !role.equals("teacher"))) {
            if (ajax) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"error\": \"Please select a valid role\"}");
                out.flush();
                return;
            } else {
                response.sendRedirect("signup.jsp?error=invalidrole");
                return;
            }
        }


        if (role.equals("student") && studentDAO.findByUsername(username) != null) {
            if (ajax) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"error\": \"Username already exists\"}");
                out.flush();
                return;
            } else {
                response.sendRedirect("signup.jsp?error=usernameexists");
                return;
            }
        } else if (role.equals("teacher") && teacherDAO.findByUsername(username) != null) {
            if (ajax) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"error\": \"Username already exists\"}");
                out.flush();
                return;
            } else {
                response.sendRedirect("signup.jsp?error=usernameexists");
                return;
            }
        } else if (!password.equals(confirmpassword)) {
            if (ajax) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"error\": \"Password not matched\"}");
                out.flush();
                return;
            } else {
                response.sendRedirect("signup.jsp?error=password");
                return;
            }
        }


        try {
          
            if (role.equals("student")) {
                Student student = new Student();
                student.setFullname(fullname);
                student.setUsername(username);
                student.setEmail(email);
                student.setPassword(password);
                studentDAO.save(student);
            } else if (role.equals("teacher")) {
                Teacher teacher = new Teacher();
                teacher.setFullname(fullname);
                teacher.setUsername(username);
                teacher.setEmail(email);
                teacher.setPassword(password);
                teacherDAO.save(teacher);
            }
            String token = JWTconfig.generateToken(username, role);

            javax.servlet.http.Cookie jwtCookie = new javax.servlet.http.Cookie("jwt", token);
            jwtCookie.setHttpOnly(true); 
            jwtCookie.setMaxAge(24 * 60 * 60);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            if (ajax) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                String redirectUrl = role.equals("student") ? "student.jsp" : "teacher.jsp";
                out.print("{\"success\": true, \"redirect\": \"" + redirectUrl + "\"}");
                out.flush();
            } else {
                if (role.equals("student")) {
                    response.sendRedirect("student.jsp");
                } else {
                    response.sendRedirect("teacher.jsp");
                }
            }
        } catch (Exception e) {
            System.err.println("SignupServlet: Exception during save: " + e.getMessage());
            e.printStackTrace();
            if (ajax) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"error\": \"Unknown error\"}");
                out.flush();
            } else {
                response.sendRedirect("signup.jsp?error=unknown");
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("signup.jsp").forward(request, response);
    }
}