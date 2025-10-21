package com.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

@WebServlet(urlPatterns = "/route")
public class ControllerServlet extends HttpServlet {

    private String getClientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        String xr = req.getHeader("X-Real-IP");
        if (xr != null && !xr.isBlank()) return xr.trim();
        return req.getRemoteAddr();
    }

    private String getAcceptedLanguages(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        Enumeration<Locale> locales = req.getLocales();
        boolean first = true;
        while (locales.hasMoreElements()) {
            if (!first) sb.append(", ");
            sb.append(locales.nextElement().toLanguageTag());
            first = false;
        }
        String raw = req.getHeader("Accept-Language");
        if (raw != null && !raw.isBlank()) sb.append(" [raw: ").append(raw).append("]");
        return sb.toString();
    }

    private void logRequest(HttpServletRequest req, String choice) {
        String method = req.getMethod();
        String ip = getClientIp(req);
        String ua = req.getHeader("User-Agent");
        String langs = getAcceptedLanguages(req);
        getServletContext().log(String.format(
                "Request -> method=%s, ip=%s, user-agent=%s, languages=%s, choice=%s",
                method, ip, ua, langs, choice));
    }

    private boolean IsBrowserUA(String ua) {
        return ua.contains("Chrome") || ua.contains("AppleWebKit") || ua.contains("Mozilla");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ua = req.getHeader("User-Agent");
        if(IsBrowserUA(ua)) {
            String choice = req.getParameter("choice");
            if (choice == null) choice = "";
            logRequest(req, choice);

            String target = "/page1.html";
            if ("2".equals(choice)) target = "/page2.html";
            resp.sendRedirect(req.getContextPath() + target);
        }
        else {
            String value = req.getParameter("value");
            if (value == null) value = "";
            logRequest(req, value);
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().print(value);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}
