package com.pochemuto.pikabu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 18.11.2015
 */
public class AuthData {
    private String sessionId;

    private Map<String, String> cookies = new ConcurrentHashMap<>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
