package com.example.Lab9.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class GatewayConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);
    
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final AtomicInteger counter = new AtomicInteger(0);

    public GatewayConfig(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = new RestTemplate();
    }

    @Bean
    public RouterFunction<ServerResponse> stableMatchRoute() {
        return GatewayRouterFunctions.route("stable-match-route")
                .route(
                        org.springframework.web.servlet.function.RequestPredicates.path("/api/stable-match/**"),
                        this::handleRequest
                )
                .build();
    }

    private ServerResponse handleRequest(ServerRequest request) {
        try {
            java.net.URI requestUri = request.uri();
            String requestPath = requestUri.getPath();

            String fullPath;
            if (!requestPath.startsWith("/api/stable-match")) {
                fullPath = "/api/stable-match" + (requestPath.startsWith("/") ? requestPath : "/" + requestPath);
            } else {
                fullPath = requestPath;
            }

            String queryString = requestUri.getQuery();
            if (queryString != null && !queryString.isEmpty()) {
                fullPath += "?" + queryString;
            }
            
            log.info("Gateway forwarding request - Request path: {}, Full path: {}, Full URI: {}", 
                    requestPath, fullPath, requestUri);

            String targetUrl;
            List<org.springframework.cloud.client.ServiceInstance> instances = 
                discoveryClient.getInstances("StableMatch");
            
            if (instances != null && !instances.isEmpty()) {
                int index = counter.getAndIncrement() % instances.size();
                org.springframework.cloud.client.ServiceInstance instance = instances.get(index);

                String baseUri = instance.getUri().toString();
                if (baseUri.endsWith("/")) {
                    baseUri = baseUri.substring(0, baseUri.length() - 1);
                }
                if (!fullPath.startsWith("/")) {
                    fullPath = "/" + fullPath;
                }
                targetUrl = baseUri + fullPath;
                log.info("Routing to service instance: {} - Target URL: {}", instance.getInstanceId(), targetUrl);
            } else {
                if (!fullPath.startsWith("/")) {
                    fullPath = "/" + fullPath;
                }
                targetUrl = "http://localhost:8081" + fullPath;
                log.warn("No service instances found, using fallback URL: {}", targetUrl);
            }

            HttpHeaders headers = new HttpHeaders();
            request.headers().asHttpHeaders().forEach((name, values) -> 
                headers.put(name, values));

            String body = null;
            try {
                body = request.body(String.class);
            } catch (Exception e) {
            }
            
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                HttpMethod.valueOf(request.method().name()),
                entity,
                String.class
            );

            ServerResponse.BodyBuilder responseBuilder = ServerResponse
                .status(response.getStatusCode());
            
            response.getHeaders().forEach((name, values) ->
                responseBuilder.header(name, values.toArray(new String[0])));
            
            return responseBuilder.body(response.getBody());
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.status(500).body("Gateway error: " + e.getMessage());
        }
    }
}

