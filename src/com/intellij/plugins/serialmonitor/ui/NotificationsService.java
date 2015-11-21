package com.intellij.plugins.serialmonitor.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry_Cherkas
 */
public class NotificationsService {

    private final NotificationGroup notificationGroup;

    public NotificationsService() {
        notificationGroup = NotificationGroup.toolWindowGroup("Serial Monitor Notification", "Serial Monitor");
    }

    public Notification createNotification(@NotNull final String content, @NotNull final NotificationType type) {
        return notificationGroup.createNotification(content, type);
    }

    public Notification createErrorNotification(@NotNull final String content) {
        return notificationGroup.createNotification(content, NotificationType.ERROR);
    }
}
