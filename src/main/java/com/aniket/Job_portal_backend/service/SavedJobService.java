package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.exception.ResourceNotFoundException;
import com.aniket.Job_portal_backend.model.Job;
import com.aniket.Job_portal_backend.model.SavedJob;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.repository.JobRepository;
import com.aniket.Job_portal_backend.repository.SavedJobRepository;
import com.aniket.Job_portal_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public SavedJobService(SavedJobRepository savedJobRepository,
                           UserRepository userRepository,
                           JobRepository jobRepository) {
        this.savedJobRepository = savedJobRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    // ⭐ Toggle save / unsave
    public void toggleSaveJob(String email, Long jobId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (savedJobRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            savedJobRepository.deleteByUserIdAndJobId(user.getId(), jobId);
        } else {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

            SavedJob savedJob = new SavedJob();
            savedJob.setUser(user);
            savedJob.setJob(job);

            savedJobRepository.save(savedJob);
        }
    }

    // ⭐ Get all saved jobs for user
    public List<SavedJob> getSavedJobs(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return savedJobRepository.findByUserId(user.getId());
    }
}