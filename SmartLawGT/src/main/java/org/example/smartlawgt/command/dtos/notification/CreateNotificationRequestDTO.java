package org.example.smartlawgt.command.dtos.notification;

import lombok.Data;

@Data
public class CreateNotificationRequestDTO {
    private String userId;
    private String title;
    private String content;
}
