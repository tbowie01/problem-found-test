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

    private LocalDateTime source_created;

    private LocalDateTime problem_created;

    public Problem(String text, String url, String source, LocalDateTime source_created) {
        this.text = text;
        this.url = url;
        this.source = source;
        this.source_created = source_created;
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

    public LocalDateTime getSource_created() {
        return source_created;
    }

    public void setSource_created(LocalDateTime source_created) {
        this.source_created = source_created;
    }

    public LocalDateTime getProblem_created() {
        return problem_created;
    }

    public void setProblem_created(LocalDateTime problem_created) {
        this.problem_created = problem_created;
    }
}
