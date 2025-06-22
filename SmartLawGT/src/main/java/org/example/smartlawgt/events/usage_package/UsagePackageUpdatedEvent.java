package org.example.smartlawgt.events.usage_package;

import lombok.Data;
import java.util.UUID;

@Data
public class UsagePackageUpdatedEvent {
    private UUID usagePackageId;
    private String name;
    private String description;
    private Float price;
    private Integer dailyLimit;
    private Integer daysLimit;
    private boolean isEnable;
}