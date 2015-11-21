package com.pochemuto.pikabu.service;

import com.pochemuto.pikabu.AuthData;
import com.pochemuto.pikabu.exception.CannotFindSessionToken;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 18.11.2015
 */
@Service
public class Authorizator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Authorizator.class);

    @Value("${pikabu.auth.page}")
    private String authPage;

    @Value("${pikabu.auth.username}")
    private String username;

    @Value("${pikabu.auth.password}")
    private String password;

    @Value("${pikabu.mainPage}")
    private String threadsPage;

    private static final Pattern SESSION_PATTERN = Pattern.compile("([\"']?)sessionID\\1\\s*:\\s*([\"'])(?<session>[a-zA-Z0-9]*)\\2");

    public AuthData authorize() throws IOException {
        AuthData data = new AuthData();
        LOGGER.debug("Try to login as {}", username);
        Connection.Response response = Jsoup.connect(threadsPage).execute();
        data.getCookies().putAll(response.cookies());
        String sessionId = extractSessionId(response);
        LOGGER.debug("found session id {}", sessionId);

        response = Jsoup.connect(authPage).cookies(data.getCookies()).data(
            "username", username,
            "password", password,
            "mode", "login"
        ).method(Connection.Method.POST)
            .header("X-Csrf-Token", sessionId)
            .header("X-Requested-With", "XMLHttpRequest")
            .ignoreContentType(true)
            .execute();

        System.out.println(response.body());

        data.setSessionId(sessionId);
        data.getCookies().putAll(response.cookies());
        return data;
    }


    private String extractSessionId(Connection.Response page) throws IOException {
        Elements scripts = page.parse().getElementsByTag("script");
        for (Element script : scripts) {
            String scriptText = script.data();
            Matcher matcher = SESSION_PATTERN.matcher(scriptText);
            if (matcher.find()) {
                return matcher.group("session");
            }
        }
        throw new CannotFindSessionToken();
    }

    @PostConstruct
    private void init() throws ConfigurationException {
        if (username.isEmpty() || password.isEmpty()) {
            throw new ConfigurationException(
                "Add 'pikabu.auth.username' and 'pikabu.auth.password' to pikabu.properties");
        }
    }
}
