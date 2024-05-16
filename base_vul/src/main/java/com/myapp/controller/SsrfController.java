package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import org.apache.http.client.fluent.Request;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;


@RestController
public class SsrfController {
    @GetMapping("/ssrf_openStream")
    public String ssrf_openStream(@RequestParam String url) throws Exception {
        URL urlObj = new URL(url);
        InputStream is = urlObj.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    @GetMapping("/ssrf_openConnection")
    public String ssrf_openConnection(@RequestParam String url) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = connection.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    @GetMapping("/ssrf_requestGet")
    public String ssrfRequestGet(@RequestParam String url) throws IOException {
        return Request.Get(url).execute().returnContent().asString();
    }

    @GetMapping("/ssrf_okhttp")
    public String ssrfOkHttp(@RequestParam String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @GetMapping("/ssrf_defaultHttpClient")
    public String ssrfDefaultHttpClient(@RequestParam String url) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
    }



}