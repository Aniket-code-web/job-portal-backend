package com.aniket.Job_portal_backend.controller;

import com.aniket.Job_portal_backend.dto.JobApplicationResponseDTO;
import com.aniket.Job_portal_backend.enums.ApplicationStatus;
import com.aniket.Job_portal_backend.model.JobApplication;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.service.JobApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    // =========================
    // APPLY FOR JOB
    // =========================
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @PostMapping(value = "/apply/{jobId}", consumes = "multipart/form-data")
    public JobApplicationResponseDTO applyForJob(
            @PathVariable Long jobId,
            @RequestParam("resume") MultipartFile resume
    ) throws IOException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        JobApplication application =
                jobApplicationService.applyForJob(email, jobId, resume);

        return mapToDTO(application);
    }


    // =========================
    // GET MY APPLICATIONS
    // =========================
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping("/my")
    public Page<JobApplicationResponseDTO> getMyApplications(Pageable pageable) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return jobApplicationService.getApplicationsByUser(email,pageable)
                .map(this::mapToDTO);
    }

    // =========================
    // RECRUITER: VIEW APPLICATIONS FOR HIS JOB
    // =========================
    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/job/{jobId}")
    public Page<JobApplicationResponseDTO> getApplicationsByJob(
            @PathVariable Long jobId,Pageable pageable) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return jobApplicationService.getApplicationsByJob(jobId, email,pageable)
                .map(this::mapToDTO);

    }

    // =========================
    // UPDATE STATUS
    // =========================
    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}/shortlist")
    public JobApplicationResponseDTO shortlist(@PathVariable Long id) {

        JobApplication app =
                jobApplicationService.updateStatus(id, ApplicationStatus.SHORTLISTED);

        return mapToDTO(app);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}/reject")
    public JobApplicationResponseDTO reject(@PathVariable Long id) {

        JobApplication app =
                jobApplicationService.updateStatus(id, ApplicationStatus.REJECTED);

        return mapToDTO(app);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}/hire")
    public JobApplicationResponseDTO hire(@PathVariable Long id) {

        JobApplication app =
                jobApplicationService.updateStatus(id, ApplicationStatus.HIRED);

        return mapToDTO(app);
    }

    // =========================
    // DTO MAPPER
    // =========================
    private JobApplicationResponseDTO mapToDTO(JobApplication app) {

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
//    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/{id}")
    public JobApplicationResponseDTO getApplicationById(@PathVariable Long id) {
        return jobApplicationService.getById(id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> withdrawApplication(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        jobApplicationService.withdrawApplication(id, email);
        return ResponseEntity.ok("Application withdrawn successfully");
    }
    @PutMapping("/{id}/notes")
    @PreAuthorize("hasRole('RECRUITER')")
    public JobApplicationResponseDTO addNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        JobApplication app =
                jobApplicationService.addRecruiterNotes(id, body.get("notes"));

        return mapToDTO(app);
    }
}
