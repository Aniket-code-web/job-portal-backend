package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.dto.UserProfileResponseDTO;
import com.aniket.Job_portal_backend.dto.UserProfileUpdateDTO;
import com.aniket.Job_portal_backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User getUser(Long id);
    List<User> getAllUser();
    User updateUser(Long id,User updatedUser);
    void deleteUser(Long id);
    Optional<User> findByEmail(String email);
    User getUserByEmail(String email);
    UserProfileResponseDTO getMyProfile(String email);
    UserProfileResponseDTO updateMyProfile(String email, UserProfileUpdateDTO dto);
}
