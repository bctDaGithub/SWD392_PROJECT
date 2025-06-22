package org.example.smartlawgt.command.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email", nullable = false, length = 60)
    private String email;

    @Column(name = "user_name", nullable = false, length = 15)
    private String userName;

    @Column(name = "password", nullable = false, length = 1024)
    private String password;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "avatar_url_text")
    private String avatarUrlText;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "role")
    private String role;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    // Relationships

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> userPackages;
}
