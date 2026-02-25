package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    Job createJob(Job job);
    Job getJobById(Long id);
    Page<Job> getAllJobs(Pageable pageable);
    Job updateJob(Long id,Job job,String email);
    void deleteJob(Long id,String email);
    Page<Job> searchByFilter(String title, String location, Double minSalary, Double maxSalary, Pageable pageable);
    Job closeJob(Long id,String email);
    Job applyJob(Job existingJob , Job updatedJob);
    List<Job> getJobsByRecruiter(Long recruiterId);
}
