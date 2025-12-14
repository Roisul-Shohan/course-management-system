package com.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/signout")
public class SignOutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cookie jwtCookie = new Cookie("jwt", "");
        jwtCookie.setMaxAge(0); 
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        response.sendRedirect("signin.jsp");
    }
}