package org.example.smartlawgt.command.dtos.Notification;

import lombok.Data;

@Data
public class CreateNotificationRequestDTO {
    private String userId;
    private String title;
    private String content;
}
