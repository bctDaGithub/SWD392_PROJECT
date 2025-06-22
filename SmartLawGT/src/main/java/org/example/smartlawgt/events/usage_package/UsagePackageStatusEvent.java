package org.example.smartlawgt.events.usage_package;

import lombok.Data;
import java.util.UUID;

@Data
public class UsagePackageStatusEvent {
    private UUID usagePackageId;
    private boolean isEnable;
}