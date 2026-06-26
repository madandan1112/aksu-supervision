package cn.aksu.supervision.dashboard.service;

import cn.aksu.supervision.alert.repository.AlertRepository;
import cn.aksu.supervision.appeal.repository.AppealRepository;
import cn.aksu.supervision.dashboard.dto.DashboardStats;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.inspection.repository.InspectionRecordRepository;
import cn.aksu.supervision.task.repository.InspectionTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final EnterpriseRepository enterpriseRepository;
    private final AppealRepository appealRepository;
    private final InspectionTaskRepository taskRepository;
    private final AlertRepository alertRepository;
    private final InspectionRecordRepository inspectionRecordRepository;

    public DashboardStats getOverview() {
        Map<String, Long> appealByStatus = new HashMap<>();
        appealByStatus.put("PENDING", appealRepository.countByStatus("PENDING"));
        appealByStatus.put("ASSIGNED", appealRepository.countByStatus("ASSIGNED"));
        appealByStatus.put("HANDLED", appealRepository.countByStatus("HANDLED"));
        appealByStatus.put("EVALUATED", appealRepository.countByStatus("EVALUATED"));

        Map<String, Long> taskByStatus = new HashMap<>();
        taskByStatus.put("PENDING", taskRepository.count());
        taskByStatus.put("IN_PROGRESS", taskRepository.count());

        Map<String, Long> alertByLevel = new HashMap<>();
        alertByLevel.put("HIGH", alertRepository.countByLevel("HIGH"));
        alertByLevel.put("MEDIUM", alertRepository.countByLevel("MEDIUM"));
        alertByLevel.put("LOW", alertRepository.countByLevel("LOW"));

        return DashboardStats.builder()
                .enterpriseCount(enterpriseRepository.count())
                .appealCount(appealRepository.count())
                .taskCount(taskRepository.count())
                .alertCount(alertRepository.count())
                .inspectionCount(inspectionRecordRepository.count())
                .appealByStatus(appealByStatus)
                .taskByStatus(taskByStatus)
                .alertByLevel(alertByLevel)
                .recentAppeals(List.of())
                .recentAlerts(List.of())
                .build();
    }

    public Map<String, Object> getIndustryView() {
        Map<String, Object> result = new HashMap<>();
        result.put("industryDistribution", enterpriseRepository.findByConditions(null, null, null, null,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)));
        return result;
    }

    public Map<String, Object> getAreaView() {
        Map<String, Object> result = new HashMap<>();
        result.put("areaDistribution", enterpriseRepository.findByConditions(null, null, null, null,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)));
        return result;
    }

    public Map<String, Object> getRiskProfile() {
        Map<String, Object> result = new HashMap<>();
        result.put("highRiskEnterprises", 0);
        result.put("mediumRiskEnterprises", 0);
        result.put("lowRiskEnterprises", enterpriseRepository.count());
        result.put("alertCount", alertRepository.count());
        result.put("unhandledAlerts", alertRepository.countByStatus("PENDING"));
        return result;
    }
}
