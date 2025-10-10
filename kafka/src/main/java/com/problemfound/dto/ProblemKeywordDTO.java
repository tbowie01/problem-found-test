package com.problemfound.dto;

public class ProblemKeywordDTO {
    private String keyword;
    private Double confidence;

    public ProblemKeywordDTO() {
    }

    public ProblemKeywordDTO(String keyword, Double confidence) {
        this.keyword = keyword;
        this.confidence = confidence;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
