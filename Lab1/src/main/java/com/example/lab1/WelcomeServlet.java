package com.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/welcome")
public class WelcomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!doctype html>");
            out.println("<html lang='en'><head><meta charset='utf-8'><title>Welcome</title></head><body>");
            out.println("<h1>Welcome</h1>");
            out.println("<p>Select a value and submit. You will be routed to Page 1 or Page 2.</p>");
            out.println("<form method='post' action='" + req.getContextPath() + "/route'>");
            out.println("<label for='choice'>Value:</label>");
            out.println("<select id='choice' name='choice'>");
            out.println("<option value='1'>1</option>");
            out.println("<option value='2'>2</option>");
            out.println("</select>");
            out.println("<button type='submit'>Go</button>");
            out.println("</form>");
            out.println("</body></html>");
        }
    }
}
