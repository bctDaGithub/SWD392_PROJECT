package org.example.smartlawgt.command.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usage_packages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsagePackageEntity {

    @Id
    @Column(name = "usage_package_id")
    private UUID usagePackageId;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "price")
    private Float price;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "days_limit")
    private Integer daysLimit;

    @Column(name = "is_enable")
    private Boolean isEnable;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "usagePackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPackageEntity> userPackages;
}
