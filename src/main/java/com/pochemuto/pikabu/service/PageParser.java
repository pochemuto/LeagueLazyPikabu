package com.pochemuto.pikabu.service;

import com.pochemuto.pikabu.dao.PikabuThread;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 17.11.2015
 */
@Service
public class PageParser {
    @Value("${pikabu.parser.selector.thread.block}")
    private String blockSelector;

    @Value("${pikabu.parser.selector.thread.title}")
    private String titleSelector;

    @Value("${pikabu.parser.selector.thread.description}")
    private String descriptionSelector;

    @Value("${pikabu.parser.selector.thread.content}")
    private String contentSelector;

    public List<PikabuThread> content(Document document) {
        Elements threadBlockElements = document.select(blockSelector);
        List<PikabuThread> threads = new ArrayList<>();
        for (Element blockElement : threadBlockElements) {
            PikabuThread thread = parseThread(blockElement);
            threads.add(thread);
        }
        return threads;
    }

    private PikabuThread parseThread(Element element) {
        PikabuThread thread = new PikabuThread();
        thread.setId(Long.parseLong(element.dataset().get("story-id")));
        thread.setTitle(element.select(titleSelector).text());
        thread.setContent(element.select(contentSelector).text());
        thread.setDescription(element.select(descriptionSelector).text());
        return thread;
    }

    private List<String> findUrls(PikabuThread thread) {
        return Collections.emptyList();
    }
}
