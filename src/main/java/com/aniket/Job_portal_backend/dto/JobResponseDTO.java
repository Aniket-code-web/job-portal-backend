package com.aniket.Job_portal_backend.dto;

import com.aniket.Job_portal_backend.enums.JobStatus;

import java.time.LocalDateTime;

public class JobResponseDTO {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String location;
    private Double salary;
    private JobStatus jobStatus;
    private LocalDateTime expiryDate;

    // Instead of full User entity → only basic info
    private Long createdById;
    private String createdByName;

    public JobResponseDTO(Long id,
                          String title,
                          String company,
                          String description,
                          String location,
                          Double salary,
                          JobStatus jobStatus,
                          LocalDateTime expiryDate,
                          Long createdById,
                          String createdByName) {

        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.jobStatus = jobStatus;
        this.expiryDate = expiryDate;
        this.createdById = createdById;
        this.createdByName = createdByName;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Double getSalary() {
        return salary;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }
}
