package cn.aksu.supervision.alert.service;

import cn.aksu.supervision.alert.entity.Alert;
import cn.aksu.supervision.alert.repository.AlertRepository;
import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional(readOnly = true)
    public PageResult<Alert> listAlerts(String status, String level, String type, int page, int size) {
        Page<Alert> pageData = alertRepository.findByConditions(status, level, type,
                PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public Alert getAlertDetail(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预警不存在"));
    }

    public Alert handleAlert(Long id, String handleResult, String handledBy) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预警不存在"));

        alert.setStatus("HANDLED");
        alert.setHandleResult(handleResult);
        alert.setHandledBy(handledBy);
        alert.setHandleTime(LocalDateTime.now());

        return alertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", alertRepository.count());
        stats.put("pending", alertRepository.countByStatus("PENDING"));
        stats.put("handled", alertRepository.countByStatus("HANDLED"));
        stats.put("highLevel", alertRepository.countByLevel("HIGH"));
        stats.put("mediumLevel", alertRepository.countByLevel("MEDIUM"));
        stats.put("lowLevel", alertRepository.countByLevel("LOW"));
        return stats;
    }
}
