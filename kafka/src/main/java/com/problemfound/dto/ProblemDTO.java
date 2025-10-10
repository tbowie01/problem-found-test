package com.problemfound.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProblemDTO {
    private String externalId;
    private String source;
    private String text;
    private String url;
    private LocalDateTime sourceCreated;
    private List<ProblemKeywordDTO> keywords;

    public ProblemDTO() {
    }

    public ProblemDTO(String externalId, String source, String text, String url, LocalDateTime sourceCreated, List<ProblemKeywordDTO> keywords) {
        this.externalId = externalId;
        this.source = source;
        this.text = text;
        this.url = url;
        this.sourceCreated = sourceCreated;
        this.keywords = keywords;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getSourceCreated() {
        return sourceCreated;
    }

    public void setSourceCreated(LocalDateTime sourceCreated) {
        this.sourceCreated = sourceCreated;
    }

    public List<ProblemKeywordDTO> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<ProblemKeywordDTO> keywords) {
        this.keywords = keywords;
    }
}
