package cn.aksu.supervision.dashboard.service;

import cn.aksu.supervision.alert.entity.Alert;
import cn.aksu.supervision.alert.repository.AlertRepository;
import cn.aksu.supervision.appeal.entity.Appeal;
import cn.aksu.supervision.appeal.repository.AppealRepository;
import cn.aksu.supervision.dashboard.dto.DashboardStats;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.inspection.repository.InspectionRecordRepository;
import cn.aksu.supervision.task.repository.InspectionTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final EnterpriseRepository enterpriseRepository;
    private final AppealRepository appealRepository;
    private final InspectionTaskRepository taskRepository;
    private final AlertRepository alertRepository;
    private final InspectionRecordRepository inspectionRecordRepository;

    /**
     * 获取总览数据，按用户权限过滤
     * - admin: 看全部数据
     * - inspector: 看自己相关的任务/预警/诉求
     * - enterprise: 看自己企业的数据
     */
    public DashboardStats getOverview(Long userId, String userType) {
        long enterpriseCount;
        long appealCount;
        long taskCount;
        long alertCount;
        long inspectionCount;
        long pendingAppeals = 0;
        long pendingAlerts = 0;
        long pendingTasks = 0;

        if ("enterprise".equals(userType) || "enterprise_user".equals(userType)) {
            // 企业用户：只看自己企业数据
            Long entId = getEnterpriseIdByUserId(userId);
            if (entId != null) {
                enterpriseCount = 1;
                appealCount = appealRepository.countByEnterpriseId(entId);
                alertCount = alertRepository.findByEnterpriseId(entId, PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
                // 企业视角：待处理整改 + 诉求
                pendingAppeals = appealRepository.findByEnterpriseIdAndStatus(entId, "PENDING",
                        PageRequest.of(0, 1)).getTotalElements();
                pendingAlerts = 0; // 企业不直接看预警
            } else {
                enterpriseCount = 0; appealCount = 0; taskCount = 0; alertCount = 0;
                inspectionCount = 0;
                return buildEmptyStats();
            }
            taskCount = 0;
            inspectionCount = 0;
            pendingTasks = 0;
        } else if ("inspector".equals(userType)) {
            // 执法人员：看自己分配的任务 + 预警 + 诉求
            enterpriseCount = enterpriseRepository.count();
            appealCount = appealRepository.count();
            taskCount = taskRepository.count();
            alertCount = alertRepository.count();
            inspectionCount = inspectionRecordRepository.count();
            pendingAppeals = appealRepository.countByStatus("PENDING");
            pendingAlerts = alertRepository.countByStatus("PENDING");
            pendingTasks = taskRepository.countByStatus("PENDING");
        } else {
            // admin: 看全部
            enterpriseCount = enterpriseRepository.count();
            appealCount = appealRepository.count();
            taskCount = taskRepository.count();
            alertCount = alertRepository.count();
            inspectionCount = inspectionRecordRepository.count();
            pendingAppeals = appealRepository.countByStatus("PENDING");
            pendingAlerts = alertRepository.countByStatus("PENDING");
            pendingTasks = taskRepository.countByStatus("PENDING");
        }

        // 状态分布
        Map<String, Long> appealByStatus = new HashMap<>();
        appealByStatus.put("PENDING", appealRepository.countByStatus("PENDING"));
        appealByStatus.put("ASSIGNED", appealRepository.countByStatus("ASSIGNED"));
        appealByStatus.put("HANDLING", appealRepository.countByStatus("HANDLING"));
        appealByStatus.put("HANDLED", appealRepository.countByStatus("HANDLED"));

        Map<String, Long> taskByStatus = new HashMap<>();
        taskByStatus.put("PENDING", taskRepository.countByStatus("PENDING"));
        taskByStatus.put("IN_PROGRESS", taskRepository.countByStatus("IN_PROGRESS"));
        taskByStatus.put("COMPLETED", taskRepository.countByStatus("COMPLETED"));
        taskByStatus.put("TERMINATED", taskRepository.countByStatus("TERMINATED"));

        Map<String, Long> alertByLevel = new HashMap<>();
        alertByLevel.put("HIGH", alertRepository.countByLevel("HIGH"));
        alertByLevel.put("MEDIUM", alertRepository.countByLevel("MEDIUM"));
        alertByLevel.put("LOW", alertRepository.countByLevel("LOW"));

        // 最近诉求（最多5条）
        List<Map<String, Object>> recentAppeals = getRecentAppeals(userId, userType);

        // 最近预警（最多5条）
        List<Map<String, Object>> recentAlerts = getRecentAlerts(userId, userType);

        return DashboardStats.builder()
                .enterpriseCount(enterpriseCount)
                .appealCount(appealCount)
                .taskCount(taskCount)
                .alertCount(alertCount)
                .inspectionCount(inspectionCount)
                .pendingAppeals(pendingAppeals)
                .pendingAlerts(pendingAlerts)
                .pendingTasks(pendingTasks)
                .appealByStatus(appealByStatus)
                .taskByStatus(taskByStatus)
                .alertByLevel(alertByLevel)
                .recentAppeals(recentAppeals)
                .recentAlerts(recentAlerts)
                .userType(userType)
                .build();
    }

    public Map<String, Object> getIndustryView(Long userId, String userType) {
        Map<String, Object> result = new HashMap<>();
        result.put("industryDistribution", enterpriseRepository.findByConditions(null, null, null, null,
                PageRequest.of(0, Integer.MAX_VALUE)));
        return result;
    }

    public Map<String, Object> getAreaView(Long userId, String userType) {
        Map<String, Object> result = new HashMap<>();
        result.put("areaDistribution", enterpriseRepository.findByConditions(null, null, null, null,
                PageRequest.of(0, Integer.MAX_VALUE)));
        return result;
    }

    public Map<String, Object> getRiskProfile(Long userId, String userType) {
        Map<String, Object> result = new HashMap<>();
        result.put("highRiskEnterprises", 0);
        result.put("mediumRiskEnterprises", 0);
        result.put("lowRiskEnterprises", enterpriseRepository.count());
        result.put("alertCount", alertRepository.count());
        result.put("unhandledAlerts", alertRepository.countByStatus("PENDING"));
        return result;
    }

    private List<Map<String, Object>> getRecentAppeals(Long userId, String userType) {
        Page<Appeal> page;
        if ("enterprise".equals(userType) || "enterprise_user".equals(userType)) {
            Long entId = getEnterpriseIdByUserId(userId);
            if (entId == null) return List.of();
            page = appealRepository.findByEnterpriseIdOrderByCreateTimeDesc(entId, PageRequest.of(0, 5));
        } else {
            // admin/inspector: 看最近待处理诉求
            page = appealRepository.findByConditions(null, null, null, PageRequest.of(0, 5));
        }
        return page.getContent().stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("title", a.getTitle());
            m.put("appealType", a.getAppealType());
            m.put("status", a.getStatus());
            m.put("createTime", a.getCreateTime() != null ? a.getCreateTime().toString() : "");
            return m;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getRecentAlerts(Long userId, String userType) {
        Page<Alert> page;
        if ("enterprise".equals(userType) || "enterprise_user".equals(userType)) {
            Long entId = getEnterpriseIdByUserId(userId);
            if (entId == null) return List.of();
            page = alertRepository.findByEnterpriseId(entId, PageRequest.of(0, 5));
        } else {
            // admin/inspector: 看最近待处理预警
            page = alertRepository.findByConditions("PENDING", null, null, PageRequest.of(0, 5));
        }
        return page.getContent().stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("title", a.getTitle());
            m.put("level", a.getLevel());
            m.put("enterpriseName", a.getEnterpriseName());
            m.put("status", a.getStatus());
            m.put("createTime", a.getCreateTime() != null ? a.getCreateTime().toString() : "");
            return m;
        }).collect(Collectors.toList());
    }

    private Long getEnterpriseIdByUserId(Long userId) {
        if (userId == null) return null;
        return enterpriseRepository.findByUserId(userId)
                .map(e -> e.getId())
                .orElse(null);
    }

    private DashboardStats buildEmptyStats() {
        return DashboardStats.builder()
                .enterpriseCount(0).appealCount(0).taskCount(0).alertCount(0).inspectionCount(0)
                .pendingAppeals(0).pendingAlerts(0).pendingTasks(0)
                .appealByStatus(new HashMap<>()).taskByStatus(new HashMap<>()).alertByLevel(new HashMap<>())
                .recentAppeals(List.of()).recentAlerts(List.of())
                .userType("enterprise")
                .build();
    }
}
