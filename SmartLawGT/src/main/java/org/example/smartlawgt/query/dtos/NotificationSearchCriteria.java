package org.example.smartlawgt.query.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSearchCriteria {
    private UUID userId;
    private Boolean isRead;
    private Boolean isEnable;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String searchText;
}
