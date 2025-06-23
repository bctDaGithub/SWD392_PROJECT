package org.example.smartlawgt.events.user_package;

import lombok.Data;
import org.example.smartlawgt.command.entities.UserPackageStatus;

@Data
public class UserPackageStatusEvent {
    private Long id;
    private UserPackageStatus status;
}
