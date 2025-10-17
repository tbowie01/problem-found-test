package com.problemfound.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problem_id;

    @Column(columnDefinition="TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(length = 50)
    private String source;

    @Column(name = "source_created")
    private LocalDateTime sourceCreated;

    @Column(name = "problem_created")
    private LocalDateTime problemCreated;

    public Problem(String text, String url, String source, LocalDateTime sourceCreated) {
        this.text = text;
        this.url = url;
        this.source = source;
        this.sourceCreated = sourceCreated;
    }

    public Problem() {

    }

    public void setProblem_id(Long id) {this.problem_id = id;}

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getSourceCreated() {
        return sourceCreated;
    }

    public void setSourceCreated(LocalDateTime source_created) {
        this.sourceCreated = source_created;
    }

    public LocalDateTime getProblemCreated() {
        return problemCreated;
    }

    public void setProblemCreated(LocalDateTime problem_created) {
        this.problemCreated = problem_created;
    }
}
