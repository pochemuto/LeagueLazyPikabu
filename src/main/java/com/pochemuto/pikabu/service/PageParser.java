package com.pochemuto.pikabu.service;

import com.pochemuto.pikabu.dao.Post;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 17.11.2015
 */
@Service
public class PageParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageParser.class);

    @Value("${pikabu.parser.selector.post.block}")
    private String blockSelector;

    @Value("${pikabu.parser.selector.post.title}")
    private String titleSelector;

    @Value("${pikabu.parser.selector.post.description}")
    private String descriptionSelector;

    @Value("${pikabu.parser.selector.post.content}")
    private String contentSelector;

    private Pattern urlPattern = Pattern.compile("http(s)?://[^ ]+");

    public List<Post> content(Document document) {
        Elements postBlockElements = document.select(blockSelector);
        List<Post> posts = new ArrayList<>();
        for (Element blockElement : postBlockElements) {
            Post post = parsePost(blockElement);
            posts.add(post);
        }
        return posts;
    }

    private Post parsePost(Element element) {
        Post post = new Post();
        post.setId(Long.parseLong(element.dataset().get("story-id")));
        post.setTitle(element.select(titleSelector).text());
        post.setContent(element.select(contentSelector).text());
        post.setDescription(element.select(descriptionSelector).text());
        post.setUrls(findUrls(post));
        return post;
    }

    private List<String> findUrls(Post post) {
        List<String> urls = new ArrayList<>();
        urls.addAll(findUrls(post.getTitle()));
        urls.addAll(findUrls(post.getDescription()));
        return urls;
    }

    public List<String> findUrls(String text) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = urlPattern.matcher(text);
        while (matcher.find()) {
            String matchedUrl = matcher.group();
            if (matchedUrl.endsWith(",") || matchedUrl.endsWith(".")) {
                matchedUrl = matchedUrl.substring(0, matchedUrl.length() - 1);
            }
            try {
                URL url = new URL(matchedUrl);
                if (!url.getHost().equalsIgnoreCase("pikabu.ru")) {
                    urls.add(matchedUrl);
                }
            } catch (MalformedURLException e) {
                LOGGER.error("Error when parsing url {}", matchedUrl);
            }
        }
        return urls;
    }
}
