package org.example.smartlawgt.command.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String userName;
    private String email;
    private String avatarUrlText;
    private LocalDate birthday;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Boolean isActive;
}
