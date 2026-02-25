package com.aniket.Job_portal_backend.repository;

import com.aniket.Job_portal_backend.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
            SELECT j FROM Job j
            WHERE (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')))
              AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:minSalary IS NULL OR j.salary >= :minSalary)
              AND (:maxSalary IS NULL OR j.salary <= :maxSalary)
            """)
    Page<Job> searchJobs(
            String title,
            String location,
            Double minSalary,
            Double maxSalary,
            Pageable pageable
    );
    List<Job> findByCreatedById(Long userId);
}