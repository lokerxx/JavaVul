package com.myapp.controller;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RestController
public class SsrfController {
    @GetMapping("/ssrf_openStream")
    public ResponseEntity<String> example(@RequestParam String url) {
        try {
            String[] allowedHosts = {"example.com", "example.net"};
            URL urlObj = new URL(url);
            if (!Arrays.asList(allowedHosts).contains(urlObj.getHost())) {
                throw new Exception("Host not allowed.");
            }
            if (!urlObj.getProtocol().equals("http") && !urlObj.getProtocol().equals("https")) {
                throw new Exception("Protocol not allowed.");
            }
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            return responseEntity;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("<html><body><h1>Error</h1><p>" + e.getMessage() + "</p></body></html>");
        }
    }

    @GetMapping("/ssrf_openConnection")
    public ResponseEntity<String> ssrf_openConnection(@RequestParam String url) {
        try {
            URL urlObj = new URL(url);

            String[] allowedHosts = {"example.com", "example.net"};
            if (!Arrays.asList(allowedHosts).contains(urlObj.getHost())) {
                throw new Exception("Host not allowed.");
            }
            if (!urlObj.getProtocol().equals("http") && !urlObj.getProtocol().equals("https")) {
                throw new Exception("Protocol not allowed.");
            }

            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("<html><body><h1>Error</h1><p>" + e.getMessage() + "</p></body></html>");
        }
    }

    @GetMapping("/ssrf_requestGet")
    public ResponseEntity<String> ssrfRequestGet(@RequestParam String url) throws IOException {
        try {
            URL urlObj = new URL(url);
            String[] allowedHosts = {"example.com", "example.net"};
            if (!Arrays.asList(allowedHosts).contains(urlObj.getHost())) {
                throw new Exception("Host not allowed.");
            }
            if (!urlObj.getProtocol().equals("http") && !urlObj.getProtocol().equals("https")) {
                throw new Exception("Protocol not allowed.");
            }

            return ResponseEntity.ok(Request.Get(url).execute().returnContent().asString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/ssrf_okhttp")
    public ResponseEntity<String> ssrfOkHttp(@RequestParam String url) throws IOException {
        try {
            URL urlObj = new URL(url);
            String[] allowedHosts = {"example.com", "example.net"};
            if (!Arrays.asList(allowedHosts).contains(urlObj.getHost())) {
                throw new Exception("Host not allowed.");
            }
            if (!urlObj.getProtocol().equals("http") && !urlObj.getProtocol().equals("https")) {
                throw new Exception("Protocol not allowed.");
            }

            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                return ResponseEntity.ok(response.body().string());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/ssrf_defaultHttpClient")
    public ResponseEntity<String> ssrfDefaultHttpClient(@RequestParam String url) throws IOException {
        try {
            URL urlObj = new URL(url);
            String[] allowedHosts = {"example.com", "example.net"};
            if (!Arrays.asList(allowedHosts).contains(urlObj.getHost())) {
                throw new Exception("Host not allowed.");
            }
            if (!urlObj.getProtocol().equals("http") && !urlObj.getProtocol().equals("https")) {
                throw new Exception("Protocol not allowed.");
            }

            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            return ResponseEntity.ok(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



}