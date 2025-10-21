package com.example.Lab2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
/*
* Groups related settings (host/port/name/user/password) into one type-safe bean.
* Easier to validate, test, and override per profile/environment.
* Generates metadata for IDE completion.
* Cleaner than scattering many @Value("${db.*}") throughout the code.
*/
@ConfigurationProperties(prefix = "db")
public class DatabaseProps {
    private boolean useH2 = true;
    private String host;
    private int port = 5432;
    private String name;
    private String user;
    private String password;

    // getters & setters
    public boolean isUseH2() { return useH2; }
    public void setUseH2(boolean useH2) { this.useH2 = useH2; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
