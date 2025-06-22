package org.example.smartlawgt.command.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UsagePackageDTO {
    private String name;
    private String description;
    private Float price;
    private Integer dailyLimit;
    private Integer daysLimit;
}
