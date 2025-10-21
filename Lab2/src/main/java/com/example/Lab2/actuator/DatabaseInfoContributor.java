package com.example.Lab2.actuator;

import com.example.Lab2.config.DatabaseProps;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DatabaseInfoContributor implements InfoContributor {

    private final DatabaseProps props;
    private final DataSource dataSource;

    public DatabaseInfoContributor(DatabaseProps props, DataSource dataSource) {
        this.props = props;
        this.dataSource = dataSource;
    }

    @Override
    public void contribute(Info.Builder builder) {
        String jdbcUrl = "unknown";
        try (Connection c = dataSource.getConnection()) {
            jdbcUrl = c.getMetaData().getURL();
        } catch (SQLException ignored) { }

        Map<String, Object> dbInfo = new LinkedHashMap<>();
        dbInfo.put("useH2", props.isUseH2());
        dbInfo.put("host", props.getHost());
        dbInfo.put("port", props.getPort());
        dbInfo.put("name", props.getName());
        dbInfo.put("jdbcUrl", jdbcUrl);

        builder.withDetail("database", dbInfo);
    }
}
