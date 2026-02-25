package com.aniket.Job_portal_backend.service;

import com.aniket.Job_portal_backend.model.Notification;
import com.aniket.Job_portal_backend.model.User;
import com.aniket.Job_portal_backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User user, String message) {
        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        notificationRepository.save(n);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadStatusFalse(userId);
    }
}