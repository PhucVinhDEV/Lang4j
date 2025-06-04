package com.example.Chat_Bot_Lang4J.AI.model;

import java.util.List;

public class BatchAddRequest {
    private List<AddDocumentRequest> documents;

    public List<AddDocumentRequest> getDocuments() { return documents; }
    public void setDocuments(List<AddDocumentRequest> documents) { this.documents = documents; }
}
