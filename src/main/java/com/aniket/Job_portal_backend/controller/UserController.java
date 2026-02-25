package com.aniket.Job_portal_backend.controller;

import com.aniket.Job_portal_backend.configuration.JwtUtil;
import com.aniket.Job_portal_backend.dto.UserProfileResponseDTO;
import com.aniket.Job_portal_backend.dto.UserProfileUpdateDTO;
import com.aniket.Job_portal_backend.dto.UserResponseDTO;
import com.aniket.Job_portal_backend.enums.Role;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // One clean constructor for everything
    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping
    public UserResponseDTO createUser(@RequestBody User user){

        if(user.getRole() == null){
            user.setRole(Role.JOB_SEEKER); // default role
        }

        if(user.getRole() == Role.ADMIN){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot create account as an admin"
            );
        }

        User savedUser = userService.createUser(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {

        User loggedInUser = getLoggedInUser();

        if (loggedInUser.getRole() == Role.ADMIN ||
                loggedInUser.getId().equals(id)) {
            User gotUser = userService.getUser(id);

            return new UserResponseDTO(
                    gotUser.getId(),
                    gotUser.getName(),
                    gotUser.getEmail(),
                    gotUser.getRole()
            );
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDTO> getAllUser(){

        return userService.getAllUser()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id,
                                      @RequestBody User user) {

        User loggedInUser = getLoggedInUser();

        if (loggedInUser.getRole() == Role.ADMIN ||
                loggedInUser.getId().equals(id)) {

            User updatedUser = userService.updateUser(id, user);

            return new UserResponseDTO(
                    updatedUser.getId(),
                    updatedUser.getName(),
                    updatedUser.getEmail(),
                    updatedUser.getRole()
            );
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest){

        User dbUser = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(
                loginRequest.getPassword(),
                dbUser.getPassword()
        )){
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                dbUser.getEmail(),
                dbUser.getRole().name()
        );

        return ResponseEntity.ok(token);
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(userService.getMyProfile(email));
    }

    // PUT /users/me
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> updateMyProfile(
            Authentication authentication,
            @RequestBody UserProfileUpdateDTO dto) {

        String email = authentication.getName();
        return ResponseEntity.ok(userService.updateMyProfile(email, dto));
    }

}
