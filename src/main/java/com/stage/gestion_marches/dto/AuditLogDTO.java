package com.stage.gestion_marches.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    private Long id;
    private String username;
    private String userRole;
    private String action;
    private String description;
    private LocalDateTime timestamp;
    private String ipAddress;
}