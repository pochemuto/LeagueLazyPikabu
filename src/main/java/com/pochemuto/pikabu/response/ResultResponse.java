package com.pochemuto.pikabu.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 21.11.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultResponse {
    @JsonProperty("result")
    private boolean ok;

    @JsonProperty
    private String message;

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }
}
