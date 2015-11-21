package com.pochemuto.pikabu.service;

import com.pochemuto.pikabu.dao.Post;
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
    @Value("${pikabu.parser.selector.post.block}")
    private String blockSelector;

    @Value("${pikabu.parser.selector.post.title}")
    private String titleSelector;

    @Value("${pikabu.parser.selector.post.description}")
    private String descriptionSelector;

    @Value("${pikabu.parser.selector.post.content}")
    private String contentSelector;

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
        return post;
    }

    private List<String> findUrls(Post post) {
        return Collections.emptyList();
    }
}
