package com.problemfound.service;

import com.problemfound.dto.ProblemDTO;
import com.problemfound.dto.ProblemKeywordDTO;
import com.problemfound.model.ProblemKeyword;
import com.problemfound.repository.ProblemKeywordRepository;
import com.problemfound.repository.ProblemRepository;
import com.problemfound.repository.RedditInfoRepository;
import com.problemfound.model.Problem;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProblemService {

    private final ProblemRepository problemRepo;
    private final ProblemKeywordRepository problemKeywordRepo;

    public ProblemService(ProblemRepository problemRepo, RedditInfoRepository redditInfoRepo, ProblemKeywordRepository problemKeywordRepo) {
        this.problemRepo = problemRepo;
        this.problemKeywordRepo = problemKeywordRepo;
    }

    @Transactional
    public Problem createProblem(ProblemDTO dto) {
        Problem problem = new Problem(dto.getText(),dto.getUrl(),dto.getSource(),dto.getSourceCreated());
        problemRepo.save(problem);

        if (dto.getKeywords() != null) {
            for (ProblemKeywordDTO kwDto : dto.getKeywords()) {
                ProblemKeyword keyword = new ProblemKeyword(problem,kwDto.getKeyword(),kwDto.getConfidence());
                problemKeywordRepo.save(keyword);
            }
        }

        return problem;
    }
}
