package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.dto.UserProfileResponseDTO;
import com.aniket.Job_portal_backend.dto.UserProfileUpdateDTO;
import com.aniket.Job_portal_backend.exception.ResourceNotFoundException;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));
    }
    @Override
    public UserProfileResponseDTO getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserProfileResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBio(),
                user.getSkills(),
                user.getExperience()
        );
    }

    @Override
    public UserProfileResponseDTO updateMyProfile(String email, UserProfileUpdateDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(dto.getName());
        user.setBio(dto.getBio());
        user.setSkills(dto.getSkills());
        user.setExperience(dto.getExperience());

        userRepository.save(user);

        return new UserProfileResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBio(),
                user.getSkills(),
                user.getExperience()
        );
    }
}
