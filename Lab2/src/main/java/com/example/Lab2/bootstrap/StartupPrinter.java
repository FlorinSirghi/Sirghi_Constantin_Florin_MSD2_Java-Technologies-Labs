package com.example.Lab2.bootstrap;

import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
public class StartupPrinter implements CommandLineRunner {
    private final JdbcTemplate jdbc;

    public StartupPrinter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== DB rows from app_info ===");
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT id, source, message FROM app_info");
        rows.forEach(r -> System.out.printf("id=%s source=%s message=%s%n", r.get("id"), r.get("source"), r.get("message")));
    }
}
