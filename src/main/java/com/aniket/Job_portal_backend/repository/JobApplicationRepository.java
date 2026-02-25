package com.aniket.Job_portal_backend.repository;

import com.aniket.Job_portal_backend.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
        boolean existsByUser_IdAndJob_Id(Long userId,Long jobId);
        Page<JobApplication> findByUserId(Long userId, Pageable pageable);
        Page<JobApplication> findByJobId(Long jobId,Pageable pageable);

}
