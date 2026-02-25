package com.aniket.Job_portal_backend.controller;

import com.aniket.Job_portal_backend.model.Notification;
import com.aniket.Job_portal_backend.repository.NotificationRepository;
import com.aniket.Job_portal_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository,
                                  UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // 🔔 Get my notifications
    @GetMapping("/my")
    public List<Notification> getMyNotifications(Authentication auth) {

        String email = auth.getName();

        Long userId = userRepository.findByEmail(email)
                .orElseThrow()
                .getId();

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ✔ mark as read
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        n.setReadStatus(true);
        notificationRepository.save(n);
    }
}