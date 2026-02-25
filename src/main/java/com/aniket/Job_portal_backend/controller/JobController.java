package com.aniket.Job_portal_backend.controller;

import com.aniket.Job_portal_backend.configuration.JwtUtil;
import com.aniket.Job_portal_backend.dto.JobRequestDTO;
import com.aniket.Job_portal_backend.dto.JobResponseDTO;
import com.aniket.Job_portal_backend.model.Job;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.repository.UserRepository;
import com.aniket.Job_portal_backend.service.JobService;
import com.aniket.Job_portal_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public JobController(JobService jobService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.jobService = jobService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 🔹 CREATE JOB
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping
    public JobResponseDTO createJob(@Valid @RequestBody JobRequestDTO dto) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setCreatedBy(user);

        Job savedJob = jobService.createJob(job);

        return mapToDTO(savedJob);
    }

    // 🔹 GET JOB BY ID
    @GetMapping("/{id}")
    public JobResponseDTO getJobById(@PathVariable Long id) {
        return mapToDTO(jobService.getJobById(id));
    }

    // 🔹 GET ALL JOBS
    @GetMapping
    public Page<JobResponseDTO> getAllJobs(Pageable pageable) {
        return jobService.getAllJobs(pageable)
                .map(this::mapToDTO);
    }

    // 🔹 UPDATE JOB
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @PutMapping("/{id}")
    public JobResponseDTO updateJob(@PathVariable Long id,
                                    @RequestBody Job job) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Job updated = jobService.updateJob(id, job, email);

        return mapToDTO(updated);
    }

    // 🔹 DELETE JOB
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        jobService.deleteJob(id, email);

        return ResponseEntity.ok("Job deleted successfully");
    }

    // 🔹 CLOSE JOB
    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}/close")
    public JobResponseDTO closeJob(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Job closed = jobService.closeJob(id, email);

        return mapToDTO(closed);
    }

    // 🔹 SEARCH
    @GetMapping("/search")
    public Page<JobResponseDTO> searchByFilter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            Pageable pageable) {

        return jobService.searchByFilter(title, location, minSalary, maxSalary, pageable)
                .map(this::mapToDTO);
    }

    // 🔹 MAPPER METHOD
    private JobResponseDTO mapToDTO(Job job) {
        return new JobResponseDTO(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getDescription(),
                job.getLocation(),
                job.getSalary(),
                job.getJobStatus(),
                job.getExpiryDate(),
                job.getCreatedBy().getId(),
                job.getCreatedBy().getName()
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<Job>> getMyJobs(HttpServletRequest request) {

        // 🔹 Extract token
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);

        // 🔹 Extract email from JWT
        String email = jwtUtil.extractUsername(token);

        // 🔹 Get user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 Fetch jobs
        List<Job> jobs = jobService.getJobsByRecruiter(user.getId());

        return ResponseEntity.ok(jobs);
    }

}
