package com.myapp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class FingerprintRouteResponse {
    private final String contentType;
    private final byte[] body;
    private final Map<String, String> headers;

    public FingerprintRouteResponse(String contentType, byte[] body, Map<String, String> headers) {
        this.contentType = contentType;
        this.body = body == null ? new byte[0] : body;
        this.headers = headers == null ? Collections.emptyMap() : new LinkedHashMap<String, String>(headers);
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
