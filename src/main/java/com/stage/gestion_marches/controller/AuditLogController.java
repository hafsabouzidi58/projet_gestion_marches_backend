package com.stage.gestion_marches.controller;
import com.stage.gestion_marches.dto.AuditLogDTO;
import com.stage.gestion_marches.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getTousLesLogs() {
        return ResponseEntity.ok(auditLogService.getTousLesLogs());
    }
}