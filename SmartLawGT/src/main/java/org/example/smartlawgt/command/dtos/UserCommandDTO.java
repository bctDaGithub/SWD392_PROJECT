package org.example.smartlawgt.command.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserCommandDTO {
    private String userName;
    private String email;
    private String password;
    private String name;
    private String avatarUrlText;
    private LocalDate birthday;
}
