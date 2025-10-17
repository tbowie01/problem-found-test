package com.problemfound.controller;

import com.problemfound.dto.ProblemDTO;
import com.problemfound.dto.ProblemFilterDTO;
import com.problemfound.service.ProblemService;
import com.problemfound.service.RedditService;
import com.problemfound.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    private final ProblemService problemService;
    private final RedditService redditService;

    public ProblemController(ProblemService problemService, RedditService redditService) {
        this.problemService = problemService;
        this.redditService = redditService;
    }

    @GetMapping
    public Page<ProblemDTO> getProblems(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ){
        ProblemFilterDTO filter = new ProblemFilterDTO(source,keyword,startDate,endDate);
        Page<Problem> problemPage = problemService.findProblems(filter,pageIndex,size);

        return problemPage.map(problem -> {
            ProblemDTO problemDTO = new ProblemDTO();
            problemDTO.setSource(problem.getSource());
            problemDTO.setText(problem.getText());
            problemDTO.setUrl(problem.getUrl());
            problemDTO.setSourceCreated(problem.getSourceCreated());
            return problemDTO;
        });
    }
}
