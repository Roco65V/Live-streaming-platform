package com.roco.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SrsCallbackRequest {
    private String action;

    @JsonProperty("client_id")
    private String clientId;

    private String ip;
    private String app;
    private String stream;
    private String param;

    @JsonProperty("page_url")
    private String pageUrl;


}