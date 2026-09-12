package cn.aksu.supervision.alert.service;

import cn.aksu.supervision.alert.constants.AlertConstants;
import cn.aksu.supervision.alert.engine.AlertRuleEngine;
import cn.aksu.supervision.alert.entity.Alert;
import cn.aksu.supervision.alert.repository.AlertRepository;
import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.system.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertRuleEngine alertRuleEngine;
    private final SysUserRepository sysUserRepository;

    @Transactional(readOnly = true)
    public PageResult<Alert> listAlerts(String status, String level, String type, int page, int size) {
        Page<Alert> pageData = alertRepository.findByConditions(status, level, type,
                PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "id")));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public Alert getAlertDetail(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预警不存在"));
    }

    /**
     * 处理预警（闭环处置）
     */
    public Alert handleAlert(Long id, String handleResult, String handledBy) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预警不存在"));

        alert.setStatus(AlertConstants.STATUS_HANDLED);
        alert.setHandleResult(handleResult);
        alert.setHandledBy(handledBy);
        alert.setHandleTime(LocalDateTime.now());

        return alertRepository.save(alert);
    }

    /**
     * 接收预警（开始处理）。PENDING/ESCALATED（督办中）均可接收。
     */
    public Alert acceptAlert(Long id, Long handlerId) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预警不存在"));

        if (!AlertConstants.STATUS_PENDING.equals(alert.getStatus())
                && !AlertConstants.STATUS_ESCALATED.equals(alert.getStatus())) {
            throw new BusinessException("预警状态不允许接收");
        }

        alert.setStatus(AlertConstants.STATUS_HANDLING);
        if (handlerId != null) {
            alert.setAssignedUserId(handlerId);
            sysUserRepository.findById(handlerId)
                    .ifPresent(u -> alert.setAssignedUserName(u.getRealName() != null ? u.getRealName() : u.getUsername()));
        }

        return alertRepository.save(alert);
    }

    /**
     * 退回预警（无法处理，需要重新分配）。仅处理中/督办中的预警可退回。
     */
    public Alert rejectAlert(Long id, String reason) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预警不存在"));

        if (!AlertConstants.STATUS_HANDLING.equals(alert.getStatus())
                && !AlertConstants.STATUS_ESCALATED.equals(alert.getStatus())) {
            throw new BusinessException("仅处理中/督办中的预警可以退回");
        }

        alert.setStatus(AlertConstants.STATUS_PENDING);
        alert.setAssignedUserId(null);
        alert.setAssignedUserName(null);
        alert.setHandleResult("退回原因: " + reason);

        return alertRepository.save(alert);
    }

    /**
     * 手动触发全量预警扫描
     */
    public int triggerManualScan() {
        log.info("手动触发预警扫描");
        return alertRuleEngine.executeScheduledRules();
    }

    /**
     * 获取预警统计（实时）
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", alertRepository.count());
        stats.put("pending", alertRepository.countByStatus(AlertConstants.STATUS_PENDING));
        stats.put("handling", alertRepository.countByStatus(AlertConstants.STATUS_HANDLING));
        stats.put("handled", alertRepository.countByStatus(AlertConstants.STATUS_HANDLED));
        stats.put("escalated", alertRepository.countByStatus(AlertConstants.STATUS_ESCALATED));
        stats.put("highLevel", alertRepository.countByLevel(AlertConstants.LEVEL_HIGH));
        stats.put("mediumLevel", alertRepository.countByLevel(AlertConstants.LEVEL_MEDIUM));
        stats.put("lowLevel", alertRepository.countByLevel(AlertConstants.LEVEL_LOW));
        return stats;
    }

    /**
     * 获取企业当前活跃预警列表
     */
    @Transactional(readOnly = true)
    public List<Alert> getEnterpriseActiveAlerts(Long enterpriseId) {
        return alertRepository.findByEnterpriseIdAndStatusIn(enterpriseId,
                List.of(AlertConstants.STATUS_PENDING, AlertConstants.STATUS_HANDLING, AlertConstants.STATUS_ESCALATED));
    }
}
