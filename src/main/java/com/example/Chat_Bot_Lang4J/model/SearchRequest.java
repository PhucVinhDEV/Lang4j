package com.example.Chat_Bot_Lang4J.model;

public class SearchRequest {
    private String query;
    private Integer topK;
    private Double minScore;

    public SearchRequest(Double minScore, String query, Integer topK) {
        this.minScore = minScore;
        this.query = query;
        this.topK = topK;
    }

    public SearchRequest() {
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }
}
