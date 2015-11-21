package com.pochemuto.pikabu.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 21.11.2015
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PageResponse {
    @JsonProperty("news_arr")
    @JsonDeserialize(contentAs = Long.class)
    private List<Long> postIds;

    @JsonProperty("news_hide")
    private boolean newsHide;

    @JsonProperty("is_feed_overflow")
    private boolean isFeedOverflow;

    @JsonProperty("html")
    private String html;

    public List<Long> getPostIds() {
        return postIds;
    }


    public boolean isFeedOverflow() {
        return isFeedOverflow;
    }

    public String getHtml() {
        return html;
    }

    public boolean isNewsHide() {
        return newsHide;
    }

    @Override
    public String toString() {
        return "AjaxResponse{" +
            "postIds=" + postIds +
            ", newsHide=" + newsHide +
            ", isFeedOverflow=" + isFeedOverflow +
            ", html='" + html + '\'' +
            '}';
    }
}
