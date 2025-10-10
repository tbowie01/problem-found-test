package com.problemfound.model;

import jakarta.persistence.*;

@Entity
@Table(name = "problem_keywords")
public class ProblemKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "problem_id",nullable = false)
    private Problem problem;

    @Column(length = 50)
    private String keyword;

    private Double confidence;

    public ProblemKeyword() {}

    public ProblemKeyword(Problem problem, String keyword, Double confidence) {
        this.problem = problem;
        this.keyword = keyword;
        this.confidence = confidence;
    }

    public Long getId() {
        return id;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
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
