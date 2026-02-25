package com.aniket.Job_portal_backend.dto;

public class UserProfileResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private String skills;
    private String experience;

    public UserProfileResponseDTO(Long id, String name, String email,
                                  String bio, String skills, String experience) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.skills = skills;
        this.experience = experience;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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
}
