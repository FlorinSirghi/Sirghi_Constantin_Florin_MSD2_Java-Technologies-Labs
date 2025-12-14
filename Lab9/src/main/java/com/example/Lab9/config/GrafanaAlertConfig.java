package com.example.Lab9.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.Arrays;

@Component
public class GrafanaAlertConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(GrafanaAlertConfig.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${grafana.url:http://localhost:3000}")
    private String grafanaUrl;

    @Value("${grafana.username:admin}")
    private String grafanaUsername;

    @Value("${grafana.password:admin}")
    private String grafanaPassword;

    @Value("${grafana.alert.enabled:true}")
    private boolean alertEnabled;

    @Override
    public void run(String... args) {
        if (!alertEnabled) {
            log.info("Grafana alert configuration is disabled");
            return;
        }

        log.info("Configuring Grafana alerts...");

        try {
            Thread.sleep(5000);

            // Step 1: Get Prometheus datasource UID
            String datasourceUid = getPrometheusDatasourceUid();
            if (datasourceUid == null) {
                log.warn("Prometheus datasource not found. Skipping alert creation.");
                log.warn("Make sure Prometheus datasource is configured in Grafana at {}", grafanaUrl);
                return;
            }

            String folderUid = createOrGetFolder();
            log.info("Using folder UID: {}", folderUid);

            createAlertRule(datasourceUid, folderUid);

            log.info("Grafana alert configuration completed successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for Grafana", e);
        } catch (Exception e) {
            log.error("Failed to configure Grafana alerts", e);
            log.error("Error details: {}", e.getMessage(), e);
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = grafanaUsername + ":" + grafanaPassword;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String getPrometheusDatasourceUid() {
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    grafanaUrl + "/api/datasources",
                    HttpMethod.GET,
                    entity,
                    Map[].class
            );

            if (response.getBody() != null) {
                for (Map<String, Object> ds : response.getBody()) {
                    String name = (String) ds.get("name");
                    String type = (String) ds.get("type");
                    if ("Prometheus".equals(name) || "prometheus".equals(type)) {
                        String uid = (String) ds.get("uid");
                        log.info("Found Prometheus datasource with UID: {}", uid);
                        return uid;
                    }
                }
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("HTTP error fetching datasources. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.warn("Could not connect to Grafana or fetch datasources: {}", e.getMessage());
            log.debug("Full error: ", e);
        }
        return null;
    }

    private String createOrGetFolder() {
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    grafanaUrl + "/api/folders",
                    HttpMethod.GET,
                    entity,
                    Map[].class
            );

            String folderName = "StableMatch";
            if (response.getBody() != null) {
                for (Map<String, Object> folder : response.getBody()) {
                    String title = (String) folder.get("title");
                    if (folderName.equals(title)) {
                        String uid = (String) folder.get("uid");
                        log.info("Found existing folder '{}' with UID: {}", folderName, uid);
                        return uid;
                    }
                }
            }

            Map<String, Object> folderRequest = new HashMap<>();
            folderRequest.put("title", folderName);
            folderRequest.put("uid", "stablematch-alerts");

            HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(folderRequest, headers);
            ResponseEntity<Map> createResponse = restTemplate.exchange(
                    grafanaUrl + "/api/folders",
                    HttpMethod.POST,
                    createEntity,
                    Map.class
            );

            if (createResponse.getBody() != null) {
                String uid = (String) createResponse.getBody().get("uid");
                log.info("Created folder '{}' with UID: {}", folderName, uid);
                return uid;
            }
        } catch (Exception e) {
            log.warn("Could not create/get folder, using default: {}", e.getMessage());
        }
        return "general";
    }

    private void createAlertRule(String datasourceUid, String folderUid) {
        try {
            HttpHeaders headers = createAuthHeaders();

            log.info("Creating alert rule with datasource UID: {}", datasourceUid);

            Map<String, Object> alertRule = new HashMap<>();
            alertRule.put("folderUID", folderUid);
            alertRule.put("ruleGroup", "StableMatch Alerts");
            alertRule.put("title", "High Memory Usage Alert");
            alertRule.put("condition", "C");
            alertRule.put("noDataState", "NoData");
            alertRule.put("execErrState", "Alerting");
            alertRule.put("for", "5m");

            Map<String, Object> queryA = new HashMap<>();
            queryA.put("refId", "A");
            
            Map<String, Integer> timeRange = new HashMap<>();
            timeRange.put("from", 600);
            timeRange.put("to", 0);
            queryA.put("relativeTimeRange", timeRange);
            
            queryA.put("datasourceUid", datasourceUid);
            queryA.put("queryType", "");

            Map<String, Object> modelA = new HashMap<>();
            modelA.put("expr", "avg(jvm_memory_used_bytes{area=\"heap\"})");
            modelA.put("refId", "A");
            modelA.put("intervalMs", 1000);
            modelA.put("maxDataPoints", 43200);
            modelA.put("editorMode", "code");
            modelA.put("format", "time_series");
            queryA.put("model", modelA);

            Map<String, Object> queryB = new HashMap<>();
            queryB.put("refId", "B");
            queryB.put("relativeTimeRange", timeRange);
            queryB.put("queryType", "");
            queryB.put("datasourceUid", "__expr__");

            Map<String, Object> modelB = new HashMap<>();
            Map<String, Object> datasourceExpr = new HashMap<>();
            datasourceExpr.put("type", "__expr__");
            datasourceExpr.put("uid", "__expr__");
            modelB.put("datasource", datasourceExpr);
            modelB.put("expression", "A");
            modelB.put("reducer", "last");
            modelB.put("refId", "B");
            modelB.put("type", "reduce");
            queryB.put("model", modelB);

            Map<String, Object> queryC = new HashMap<>();
            queryC.put("refId", "C");
            queryC.put("relativeTimeRange", timeRange);
            queryC.put("queryType", "");
            queryC.put("datasourceUid", "__expr__");

            Map<String, Object> modelC = new HashMap<>();
            Map<String, Object> datasourceExprC = new HashMap<>();
            datasourceExprC.put("type", "__expr__");
            datasourceExprC.put("uid", "__expr__");
            modelC.put("datasource", datasourceExprC);
            modelC.put("expression", "B");
            modelC.put("refId", "C");
            modelC.put("type", "threshold");
            modelC.put("hide", false);

            List<Map<String, Object>> conditions = new ArrayList<>();
            Map<String, Object> thresholdCondition = new HashMap<>();
            thresholdCondition.put("evaluator", new HashMap<String, Object>() {{
                put("params", Arrays.asList(20971520L));  // 20 MB threshold for testing
                put("type", "gt");
            }});
            thresholdCondition.put("operator", new HashMap<String, String>() {{
                put("type", "and");
            }});
            thresholdCondition.put("query", new HashMap<String, Object>() {{
                put("params", Arrays.asList("B"));
            }});
            thresholdCondition.put("reducer", new HashMap<String, Object>() {{
                put("params", new ArrayList<>());
                put("type", "last");
            }});
            thresholdCondition.put("type", "query");
            conditions.add(thresholdCondition);
            
            modelC.put("conditions", conditions);
            queryC.put("model", modelC);

            alertRule.put("data", Arrays.asList(queryA, queryB, queryC));

            Map<String, String> annotations = new HashMap<>();
            annotations.put("description", "Memory usage has exceeded 20 MB (20971520 bytes). Current value: {{ $values.A }} bytes");
            annotations.put("summary", "High Memory Usage - Memory exceeds 20 MB threshold");
            alertRule.put("annotations", annotations);

            Map<String, String> labels = new HashMap<>();
            labels.put("severity", "warning");
            labels.put("service", "StableMatch");
            alertRule.put("labels", labels);

            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String requestBody = objectMapper.writeValueAsString(alertRule);
            log.info("Alert rule JSON payload length: {} chars", requestBody.length());
            log.info("Alert rule JSON: {}", requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            log.info("Checking for and deleting existing alert rules...");
            try {
                String getUrl = grafanaUrl + "/api/v1/provisioning/alert-rules";
                ResponseEntity<Map[]> getResponse = restTemplate.exchange(
                        getUrl,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        Map[].class
                );
                
                if (getResponse.getBody() != null) {
                    int deletedCount = 0;
                    for (Map<String, Object> rule : getResponse.getBody()) {
                        String uid = (String) rule.get("uid");
                        String title = (String) rule.get("title");
                        // Delete if it matches our alert UID or title
                        if ("high-memory-usage".equals(uid) || "High Memory Usage Alert".equals(title)) {
                            try {
                                String deleteUrl = grafanaUrl + "/api/v1/provisioning/alert-rules/" + uid;
                                restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
                                log.info("Deleted existing alert rule - UID: {}, Title: {}", uid, title);
                                deletedCount++;
                            } catch (Exception deleteEx) {
                                log.warn("Failed to delete alert rule {}: {}", uid, deleteEx.getMessage());
                            }
                        }
                    }
                    if (deletedCount == 0) {
                        log.info("No existing alert rules found to delete");
                    } else {
                        log.info("Successfully deleted {} existing alert rule(s)", deletedCount);
                        Thread.sleep(500);
                    }
                } else {
                    log.info("No alert rules found in Grafana");
                }
            } catch (Exception e) {
                log.warn("Error while checking for existing alert rules: {}. Will proceed with creation.", e.getMessage());
            }

            String createUrl = grafanaUrl + "/api/v1/provisioning/alert-rules";
            log.info("Creating alert rule using Provisioning API at: {}", createUrl);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    createUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✓ Successfully created Grafana alert rule: High Memory Usage Alert");
                log.info("✓ View alerts at: {}/alerting/rules", grafanaUrl);
            } else {
                log.error("✗ Failed to create alert rule. Status: {}, Response: {}", 
                        response.getStatusCode(), response.getBody());
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("HTTP error creating Grafana alert rule. Status: {}", e.getStatusCode());
            log.error("Response body: {}", e.getResponseBodyAsString());
            log.error("Full error: ", e);
        } catch (Exception e) {
            log.error("Error creating Grafana alert rule", e);
            log.error("Exception type: {}, Message: {}", e.getClass().getName(), e.getMessage());
            if (e.getCause() != null) {
                log.error("Cause: {}", e.getCause().getMessage());
            }
        }
    }
}

