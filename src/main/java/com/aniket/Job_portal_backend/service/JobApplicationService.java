package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.dto.JobApplicationResponseDTO;
import com.aniket.Job_portal_backend.enums.ApplicationStatus;
import com.aniket.Job_portal_backend.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface JobApplicationService {
    JobApplication applyForJob(String email, Long jobId, MultipartFile resume) throws IOException;
    Page<JobApplication> getApplicationsByUser(Long userId, Pageable pageable);
    Page<JobApplication> getApplicationsByJob(Long jobId,String email,Pageable pageable);
    JobApplication updateStatus(Long applicationId, ApplicationStatus status);
    Page<JobApplication> getApplicationsByUser(String email,Pageable pageable);
    JobApplicationResponseDTO getById(Long id);
    void withdrawApplication(Long id, String email);
    JobApplication addRecruiterNotes(Long id, String notes);
}
