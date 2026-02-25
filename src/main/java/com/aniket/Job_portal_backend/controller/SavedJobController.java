package com.aniket.Job_portal_backend.controller;

import com.aniket.Job_portal_backend.model.SavedJob;
import com.aniket.Job_portal_backend.service.SavedJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saved-jobs")
public class SavedJobController {

    private final SavedJobService savedJobService;

    public SavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    // ⭐ Toggle save job
    @PostMapping("/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> toggleSave(@PathVariable Long jobId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        savedJobService.toggleSaveJob(email, jobId);

        return ResponseEntity.ok("Saved jobs updated");
    }

    // ⭐ Get saved jobs list
    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public List<SavedJob> getMySavedJobs() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return savedJobService.getSavedJobs(email);
    }
}
