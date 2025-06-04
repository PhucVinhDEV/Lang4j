package com.example.Chat_Bot_Lang4J.AI.model;

import java.util.Map;

public class AddDocumentRequest {
    private String text;
    private Map<String, String> metadata;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AddDocumentRequest() {
    }

    public AddDocumentRequest(Map<String, String> metadata, String text) {
        this.metadata = metadata;
        this.text = text;
    }
}
