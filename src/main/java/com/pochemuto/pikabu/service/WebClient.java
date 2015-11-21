package com.pochemuto.pikabu.service;

import com.pochemuto.pikabu.AuthData;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 21.11.2015
 */
@Service
public class WebClient {

    @Value("${pikabu.request.timeoutMsec}")
    private int requestTimeout;

    @Autowired
    private Authorizator authorizator;

    private AuthData authData;

    public Connection connect(String url) {
        return Jsoup.connect(url)
            .ignoreContentType(true)
            .timeout(requestTimeout)
            .cookies(authData.getCookies());
    }

    public Connection ajax(String url) {
        return connect(url)
            .header("Accept", "application/json, text/javascript, */*; q=0.01")
            .header("X-Csrf-Token", authData.getSessionId())
            .header("X-Requested-With", "XMLHttpRequest");
    }

    @PostConstruct
    private void init() throws IOException {
        authData = authorizator.authorize();
    }

}
