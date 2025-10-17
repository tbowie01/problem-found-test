package com.problemfound.dto;

import java.time.LocalDate;

public class ProblemFilterDTO {
    private String source;
    private String keyword;
    private LocalDate startDate;
    private LocalDate endDate;

    public ProblemFilterDTO() {
    }

    public ProblemFilterDTO(String source, String keyword, LocalDate startDate, LocalDate endDate) {
        this.source = source;
        this.keyword = keyword;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
