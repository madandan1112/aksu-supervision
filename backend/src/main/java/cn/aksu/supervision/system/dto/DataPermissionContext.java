package cn.aksu.supervision.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPermissionContext {

    private Long userId;
    private String userType;
    private String dataScope;
    private String regionScope;
    private String deptScope;
    private String orgLevel;
    private Long orgId;
    private Set<Long> regionIds;
    private Set<Long> cityIds;
    private Set<Long> countyIds;
    private Set<Long> deptIds;
    private Set<Long> userOrgIds;

    public boolean isAdmin() {
        return "ADMIN".equals(dataScope) || "ALL".equals(dataScope);
    }

    public boolean isRegionLevel() {
        return "REGION".equals(regionScope) || "ALL".equals(regionScope);
    }

    public boolean isCityLevel() {
        return "CITY".equals(regionScope);
    }

    public boolean isCountyLevel() {
        return "COUNTY".equals(regionScope);
    }

    public boolean isDeptLevel() {
        return "DEPT".equals(deptScope) || "ALL".equals(deptScope);
    }

    public boolean isSelfOnly() {
        return "SELF".equals(dataScope);
    }
}
