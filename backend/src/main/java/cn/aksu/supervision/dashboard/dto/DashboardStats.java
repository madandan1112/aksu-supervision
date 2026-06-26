package cn.aksu.supervision.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    private long enterpriseCount;
    private long appealCount;
    private long taskCount;
    private long alertCount;
    private long inspectionCount;
    private Map<String, Long> appealByStatus;
    private Map<String, Long> taskByStatus;
    private Map<String, Long> alertByLevel;
    private List<Map<String, Object>> recentAppeals;
    private List<Map<String, Object>> recentAlerts;
}
