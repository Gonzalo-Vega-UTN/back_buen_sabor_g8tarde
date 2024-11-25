package com.example.buensaborback.bussines.service.impl;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Auth0Service {

    @Getter
    @Value("${auth0.api.domain}")
    private String domain;

    @Value("${auth0.api.client.id}")
    private String clientId;

    @Value("${auth0.api.client.secret}")
    private String clientSecret;

    @Value("${auth0.api.audience}")
    private String audience;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getManagementApiToken() {
        String tokenDomain = String.valueOf(domain);
        if (tokenDomain.endsWith("/")) {
            tokenDomain = tokenDomain.substring(0, domain.length() - 1);
        }
        tokenDomain = tokenDomain.replace("/api/v2", "");
        String url = String.format("%s%s", tokenDomain, "/oauth/token");
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("audience", audience);
        body.put("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        System.out.println(response);
        return (String) response.getBody().get("access_token");
    }
}

/*

 body.put("client_id", "ovZCArk7yf5S9ehc3ObBU23xladyB7Oj");
        body.put("client_secret", "WNDZ3-Is09ohk7VHcalXO2-0RTFnLKJ3ewVEYDT0POFUOQVUAZqefzAuRSHuFKTZ");
        body.put("audience", "https://dev-f4nopdn0rasjegoe.us.auth0.com/api/v2/");
        body.put("grant_type", "client_credentials");
 */