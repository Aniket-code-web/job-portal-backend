package com.aniket.Job_portal_backend.dto;

import java.time.LocalDateTime;

public class JobApplicationResponseDTO {

        private Long id;


        private Long jobId;
        private String company;
        private String jobTitle;

        private Long applicantId;
        private String applicantName;
        private String applicantEmail;

        private String status;

        private LocalDateTime appliedAt;
        private String resumeUrl;

    private String bio;
    private String skills;
    private String experience;
    private String recruiterNotes;

    public JobApplicationResponseDTO(Long id,
                                     Long jobId,
                                     String company,
                                     String jobTitle,
                                     Long applicantId,
                                     String applicantName,
                                     String applicantEmail,
                                     String status,
                                     LocalDateTime appliedAt,
                                     String resumeUrl,
                                     String bio,
                                     String skills,
                                     String experience,
                                     String recruiterNotes) {

            this.id = id;
            this.jobId = jobId;
            this.company=company;
            this.jobTitle = jobTitle;
            this.applicantId = applicantId;
            this.applicantName = applicantName;
            this.applicantEmail=applicantEmail;
            this.status = status;
            this.appliedAt = appliedAt;
            this.resumeUrl=resumeUrl;
            this.bio=bio;
            this.skills=skills;
            this.experience=experience;
            this.recruiterNotes=recruiterNotes;

        }

    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getCompany() {
        return company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }
    public String getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getSkills() {
        return skills;
    }

    public String getExperience() {
        return experience;
    }

    public String getRecruiterNotes() {
        return recruiterNotes;
    }
}
