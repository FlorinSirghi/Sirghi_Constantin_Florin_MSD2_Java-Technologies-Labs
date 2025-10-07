package com.example.lab1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(urlPatterns = "/api/echo")
public class ApiEchoServlet extends HttpServlet {

    private void logRequest(HttpServletRequest req, String value) {
        String method = req.getMethod();
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        String ua = req.getHeader("User-Agent");
        String langs = req.getHeader("Accept-Language");
        req.getServletContext().log(String.format(
                "API Request -> method=%s, ip=%s, user-agent=%s, languages=%s, value=%s",
                method, ip, ua, langs, value));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String value = req.getParameter("value");
        if (value == null) value = "";
        logRequest(req, value);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().print(value);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String value = req.getParameter("value");
        if (value == null) value = "";
        logRequest(req, value);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().print(value);
    }
}