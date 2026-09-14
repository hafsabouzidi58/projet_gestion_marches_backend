package com.stage.gestion_marches.service;
import com.stage.gestion_marches.dto.AuditLogDTO;
import com.stage.gestion_marches.entity.AuditLog;
import com.stage.gestion_marches.repository.AuditLogRepository;
import com.stage.gestion_marches.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public List<AuditLogDTO> getTousLesLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AuditLogDTO mapToDTO(AuditLog log) {
        return AuditLogDTO.builder()
                .id(log.getId())
                .username(log.getUsername())
                .userRole(log.getUserRole())
                .action(log.getAction())
                .description(log.getDescription())
                .timestamp(log.getTimestamp())
                .ipAddress(log.getIpAddress())
                .build();
    }
}