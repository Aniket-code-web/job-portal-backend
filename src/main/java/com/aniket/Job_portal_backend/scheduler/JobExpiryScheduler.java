package com.aniket.Job_portal_backend.scheduler;

import com.aniket.Job_portal_backend.enums.JobStatus;
import com.aniket.Job_portal_backend.model.Job;
import com.aniket.Job_portal_backend.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobExpiryScheduler {

    private final JobRepository jobRepository;

    public JobExpiryScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void expireJobs(){
        List<Job> jobs = jobRepository.findAll();
        for(Job job : jobs){
            if (job.getJobStatus() == JobStatus.OPEN &&
                    job.getExpiryDate().isBefore(LocalDateTime.now())) {

                job.setJobStatus(JobStatus.EXPIRED);
                jobRepository.save(job);
            }
        }
    }
}
