package com.stage.gestion_marches.service;
import com.stage.gestion_marches.dto.AuditLogDTO;
import java.util.List;

public interface AuditLogService {
    List<AuditLogDTO> getTousLesLogs();
}
