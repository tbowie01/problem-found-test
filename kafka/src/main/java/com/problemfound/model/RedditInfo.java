package com.problemfound.model;

import jakarta.persistence.*;

@Entity
@Table(name="reddit_infos")
public class RedditInfo {
    @OneToOne(optional = false)
    @JoinColumn(name = "problem_id",referencedColumnName = "problem_id",nullable = false)
    private Problem problem;

    @Id
    @Column(length = 20)
    private String reddit_id;

    @Column(length = 30)
    private String subreddit;

    @Column(columnDefinition = "TEXT")
    private String post;

    @Column(columnDefinition = "TEXT")
    private String comment;

    public RedditInfo(Problem problem, String reddit_id, String subreddit, String post, String comment) {
        this.problem = problem;
        this.reddit_id = reddit_id;
        this.subreddit = subreddit;
        this.post = post;
        this.comment = comment;
    }

    public RedditInfo() {

    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public String getReddit_id() {
        return reddit_id;
    }

    public void setReddit_id(String reddit_id) {
        this.reddit_id = reddit_id;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
