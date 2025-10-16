package com.problemfound.service;

import com.problemfound.model.Problem;
import com.problemfound.model.RedditInfo;
import com.problemfound.repository.RedditInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedditService {

    private final RedditInfoRepository redditInfoRepo;

    public RedditService(RedditInfoRepository redditInfoRepo) {
        this.redditInfoRepo = redditInfoRepo;
    }

    @Transactional
    public RedditInfo createRedditInfo(Problem problem, String reddit_id, String subreddit, String post, String comment) {
        RedditInfo redditInfo = new RedditInfo(problem,reddit_id,subreddit,post,comment);
        redditInfoRepo.save(redditInfo);
        return redditInfo;
    }

    @Transactional
    public Boolean checkIfRedditExists(String reddit_id) {
        Optional<RedditInfo> redditInfo = redditInfoRepo.findById(reddit_id);
        return redditInfo.isPresent();
    }
}
