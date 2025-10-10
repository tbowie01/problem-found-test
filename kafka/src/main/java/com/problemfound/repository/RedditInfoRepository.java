package com.problemfound.repository;

import com.problemfound.model.RedditInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditInfoRepository extends JpaRepository<RedditInfo,String> {
}
