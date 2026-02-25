package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.dto.JobApplicationResponseDTO;
import com.aniket.Job_portal_backend.enums.ApplicationStatus;
import com.aniket.Job_portal_backend.enums.JobStatus;
import com.aniket.Job_portal_backend.enums.Role;
import com.aniket.Job_portal_backend.exception.ResourceNotFoundException;
import com.aniket.Job_portal_backend.exception.UnauthorizedAccessException;
import com.aniket.Job_portal_backend.model.Job;
import com.aniket.Job_portal_backend.model.JobApplication;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.repository.JobApplicationRepository;
import com.aniket.Job_portal_backend.repository.JobRepository;
import com.aniket.Job_portal_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobApplicationServiceImplementation implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;

    public JobApplicationServiceImplementation(
            JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            NotificationService notificationService) {

        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.notificationService = notificationService;
    }

    @Override
    public JobApplication applyForJob(String email, Long jobId, MultipartFile resume) throws IOException {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (jobApplicationRepository.existsByUser_IdAndJob_Id(user.getId(), jobId)) {
            throw new UnauthorizedAccessException("Already applied");
        }

        // 📁 Save file locally
        String uploadDir = "uploads/";
        String fileName = System.currentTimeMillis() + "_" + resume.getOriginalFilename();

        Path filePath = Paths.get(uploadDir + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, resume.getBytes());

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setUser(user);
        application.setApplicationStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());

        // ⭐ Save URL
        application.setResumeUrl("/uploads/" + fileName);

        return jobApplicationRepository.save(application);
    }

    @Override
    public Page<JobApplication> getApplicationsByUser(Long userId,Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return jobApplicationRepository.findByUserId(userId,pageable);
    }

    @Override
    public Page<JobApplication> getApplicationsByJob(Long jobId,
                                                     String email,
                                                     Pageable pageable) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + jobId));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));

        if (user.getRole() == Role.ADMIN) {
            return jobApplicationRepository.findByJobId(jobId, pageable);
        }

        if (user.getRole() == Role.RECRUITER &&
                job.getCreatedBy().getId().equals(user.getId())) {

            return jobApplicationRepository.findByJobId(jobId, pageable);
        }

        throw new UnauthorizedAccessException(
                "You are not allowed to view applications for this job");
    }

    @Override
    public JobApplication updateStatus(Long applicationId, ApplicationStatus status) {

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with id: " + applicationId));

        application.setApplicationStatus(status);

        JobApplication savedApp = jobApplicationRepository.save(application);

        // 🔔 CREATE NOTIFICATION
        User user = savedApp.getUser();

        String message = "Your application for "
                + savedApp.getJob().getTitle()
                + " at " + savedApp.getJob().getCompany()
                + " is now " + status.name();

        notificationService.createNotification(user, message);

        return savedApp;
    }

    @Override
    public Page<JobApplication> getApplicationsByUser(String email, Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));

        return jobApplicationRepository.findByUserId(user.getId(),pageable);
    }
    @Override
    public JobApplicationResponseDTO getById(Long id) {

        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));

        User user = app.getUser();

        return new JobApplicationResponseDTO(
                app.getId(),
                app.getJob().getId(),
                app.getJob().getCompany(),
                app.getJob().getTitle(),
                app.getUser().getId(),
                app.getUser().getName(),
                app.getUser().getEmail(),
                app.getApplicationStatus().name(),
                app.getAppliedAt(),
                app.getResumeUrl(),
                user.getBio(),
                user.getSkills(),
                user.getExperience(),
                app.getRecruiterNotes()
        );
    }
    @Override
    public void withdrawApplication(Long id, String email) {

        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        // Only applicant can withdraw
        if (!app.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You cannot withdraw this application");
        }

        jobApplicationRepository.delete(app);
    }
    @Override
    public JobApplication addRecruiterNotes(Long id, String notes) {

        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.setRecruiterNotes(notes);

        return jobApplicationRepository.save(app);
    }

}
