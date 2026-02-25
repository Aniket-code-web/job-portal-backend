package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.enums.JobStatus;
import com.aniket.Job_portal_backend.enums.Role;
import com.aniket.Job_portal_backend.exception.ResourceNotFoundException;
import com.aniket.Job_portal_backend.exception.UnauthorizedAccessException;
import com.aniket.Job_portal_backend.model.Job;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.repository.JobRepository;
import com.aniket.Job_portal_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobServiceImplementation implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobServiceImplementation(JobRepository jobRepository,
                                    UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Job createJob(Job job) {
        job.setJobStatus(JobStatus.OPEN);
        job.setExpiryDate(LocalDateTime.now().plusDays(30));
        return jobRepository.save(job);
    }

    @Override
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + id));
    }

    @Override
    public Page<Job> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    @Override
    public Job updateJob(Long id, Job updatedJob, String email) {

        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));

        // ADMIN can update any job
        if (user.getRole() == Role.ADMIN) {
            return applyJob(existingJob, updatedJob);
        }

        // RECRUITER can update only own job
        if (user.getRole() == Role.RECRUITER &&
                existingJob.getCreatedBy().getId().equals(user.getId())) {

            return applyJob(existingJob, updatedJob);
        }

        throw new UnauthorizedAccessException("You cannot update this job");
    }

    @Override
    public void deleteJob(Long id, String email) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));

        // ADMIN can delete any job
        if (user.getRole() == Role.ADMIN) {
            jobRepository.delete(job);
            return;
        }

        // RECRUITER can delete only own job
        if (user.getRole() == Role.RECRUITER &&
                job.getCreatedBy().getId().equals(user.getId())) {

            jobRepository.delete(job);
            return;
        }

        throw new UnauthorizedAccessException("You cannot delete this job");
    }

    @Override
    public Page<Job> searchByFilter(String title,
                                    String location,
                                    Double minSalary,
                                    Double maxSalary,
                                    Pageable pageable) {
        return jobRepository.searchJobs(title, location, minSalary, maxSalary, pageable);
    }

    @Override
    public Job closeJob(Long id, String email) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));

        // ADMIN can close any job
        if (user.getRole() == Role.ADMIN) {
            job.setJobStatus(JobStatus.CLOSED);
            return jobRepository.save(job);
        }

        // RECRUITER can close only own job
        if (user.getRole() == Role.RECRUITER &&
                job.getCreatedBy().getId().equals(user.getId())) {

            job.setJobStatus(JobStatus.CLOSED);
            return jobRepository.save(job);
        }

        throw new UnauthorizedAccessException("You cannot close this job");
    }

    @Override
    public Job applyJob(Job existingJob, Job updatedJob) {

        if (updatedJob.getTitle() != null) {
            existingJob.setTitle(updatedJob.getTitle());
        }

        if (updatedJob.getCompany() != null) {
            existingJob.setCompany(updatedJob.getCompany());
        }

        if (updatedJob.getDescription() != null) {
            existingJob.setDescription(updatedJob.getDescription());
        }

        if (updatedJob.getLocation() != null) {
            existingJob.setLocation(updatedJob.getLocation());
        }

        if (updatedJob.getSalary() != null) {
            existingJob.setSalary(updatedJob.getSalary());
        }

        if (updatedJob.getJobStatus() != null) {
            existingJob.setJobStatus(updatedJob.getJobStatus());
        }

        if (updatedJob.getExpiryDate() != null) {
            existingJob.setExpiryDate(updatedJob.getExpiryDate());
        }

        return jobRepository.save(existingJob);
    }

    public List<Job> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByCreatedById(recruiterId);
    }
}
