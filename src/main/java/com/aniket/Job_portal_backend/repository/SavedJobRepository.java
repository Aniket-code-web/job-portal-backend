package com.aniket.Job_portal_backend.repository;

import com.aniket.Job_portal_backend.model.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    void deleteByUserIdAndJobId(Long userId, Long jobId);

    List<SavedJob> findByUserId(Long userId);
}
