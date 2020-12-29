package com.intellij.plugins.serialmonitor.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry_Cherkas
 */
public class NotificationsService {

    private final NotificationGroup notificationGroup;

    public static NotificationsService getInstance() {
        return ServiceManager.getService(NotificationsService.class);
    }

    public NotificationsService() {
        notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Serial Monitor Notification");
    }

    public Notification createNotification(@NotNull final String content, @NotNull final NotificationType type) {
        return notificationGroup.createNotification(content, type);
    }

    public Notification createErrorNotification(@NotNull final String content) {
        return notificationGroup.createNotification(content, NotificationType.ERROR);
    }
}
