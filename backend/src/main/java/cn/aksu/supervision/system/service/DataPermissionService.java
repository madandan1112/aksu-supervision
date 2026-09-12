package cn.aksu.supervision.system.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.system.dto.DataPermissionContext;
import cn.aksu.supervision.system.entity.*;
import cn.aksu.supervision.system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPermissionService {

    private final SysUserRepository sysUserRepository;
    private final SysRoleRepository sysRoleRepository;
    private final UserDataScopeRepository userDataScopeRepository;
    private final OrgStructureRepository orgStructureRepository;
    private final OrgUserPositionRepository orgUserPositionRepository;

    /**
     * 根据当前用户ID获取数据权限范围
     */
    public DataPermissionContext getDataPermission(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 超级管理员（admin用户类型或用户名）直接返回全量权限
        if ("admin".equals(user.getUserType()) || "admin".equals(user.getUsername())) {
            return DataPermissionContext.builder()
                    .userId(userId)
                    .dataScope("ALL")
                    .regionScope("ALL")
                    .deptScope("ALL")
                    .regionIds(Set.of(1L))
                    .cityIds(Collections.emptySet())
                    .countyIds(Collections.emptySet())
                    .deptIds(Collections.emptySet())
                    .build();
        }

        // 获取用户角色
        SysRole role = null;
        if (user.getRoleId() != null) {
            role = sysRoleRepository.findById(user.getRoleId()).orElse(null);
        }
        if (role == null) {
            role = SysRole.builder().dataScope("SELF").regionScope("SELF").deptScope("SELF").build();
        }

        // 获取用户关联的组织
        List<UserDataScope> userScopes = userDataScopeRepository.findByUserId(userId);

        // 获取用户岗位关联的组织
        List<OrgUserPosition> userPositions = orgUserPositionRepository.findByUserId(userId);
        Set<Long> userOrgIds = userPositions.stream()
                .map(OrgUserPosition::getOrgId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 获取用户所在科室（从组织架构）
        Set<Long> deptIds = new HashSet<>();
        Set<Long> regionIds = new HashSet<>();
        Set<Long> cityIds = new HashSet<>();
        Set<Long> countyIds = new HashSet<>();

        for (Long orgId : userOrgIds) {
            OrgStructure org = orgStructureRepository.findById(orgId).orElse(null);
            if (org == null) continue;

            Integer level = org.getLevel();
            if (level == 1) regionIds.add(orgId);
            else if (level == 2) cityIds.add(orgId);
            else if (level == 3) countyIds.add(orgId);
            else if (level == 4) deptIds.add(orgId);

            // 获取上级组织
            if (org.getParentId() != null) {
                OrgStructure parent = orgStructureRepository.findById(org.getParentId()).orElse(null);
                if (parent != null) {
                    if (parent.getLevel() == 1) regionIds.add(parent.getId());
                    else if (parent.getLevel() == 2) cityIds.add(parent.getId());
                    else if (parent.getLevel() == 3) countyIds.add(parent.getId());
                }
            }
        }

        // 补充用户自定义数据权限范围
        for (UserDataScope scope : userScopes) {
            switch (scope.getOrgType()) {
                case "REGION" -> regionIds.add(scope.getOrgId());
                case "CITY" -> cityIds.add(scope.getOrgId());
                case "COUNTY" -> countyIds.add(scope.getOrgId());
                case "DEPT" -> deptIds.add(scope.getOrgId());
            }
        }

        return DataPermissionContext.builder()
                .userId(userId)
                .userType(user.getUserType())
                .dataScope(role.getDataScope())
                .regionScope(role.getRegionScope())
                .deptScope(role.getDeptScope())
                .orgLevel(role.getOrgLevel())
                .orgId(role.getOrgId())
                .regionIds(regionIds)
                .cityIds(cityIds)
                .countyIds(countyIds)
                .deptIds(deptIds)
                .userOrgIds(userOrgIds)
                .build();
    }

    /** 地区级/全量可见的完整区域清单（与组织架构 level=2 的县市短名保持一致） */
    private static final List<String> ALL_AREAS = List.of(
            "阿克苏市", "库车市", "温宿县", "拜城县", "新和县", "沙雅县", "乌什县", "阿瓦提县", "柯坪县");

    /**
     * 组织名 → 企业area短名 归一化。
     * 组织架构中 level=2 存短名（"阿克苏市"），level=3 存全名（"阿克苏市市场监督管理局"），
     * 企业 area 字段存的是短名，两者必须归一后才能匹配。
     * 无法归出区域名的（如"地区局（机关）"）返回 null。
     */
    private String toAreaName(String orgName) {
        if (orgName == null || orgName.isBlank()) return null;
        String name = orgName.trim();
        // 剥离局机关后缀（含"XX分局"等变体）
        name = name.replace("市场监督管理局", "");
        name = name.replace("（机关）", "").replace("(机关)", "");
        if (name.endsWith("分局")) {
            name = name.substring(0, name.length() - 2);
        }
        name = name.trim();
        if (name.isEmpty() || name.contains("地区")) return null;
        return name;
    }

    /**
     * 检查用户是否对某企业有数据权限
     */
    public boolean hasEnterprisePermission(Long userId, String enterpriseArea, String enterpriseIndustry, Long enterpriseDeptId) {
        DataPermissionContext ctx = getDataPermission(userId);

        // 系统管理员全权限
        if ("ADMIN".equals(ctx.getDataScope()) || "ALL".equals(ctx.getDataScope())) {
            return true;
        }

        // 仅看自己（企业用户只能看自己）
        if ("SELF".equals(ctx.getDataScope())) {
            return false; // 需要额外关联判断
        }

        // 按区域判断（用归一化后的短名互相匹配）
        if (ctx.getRegionScope() != null && !"ALL".equals(ctx.getRegionScope())) {
            switch (ctx.getRegionScope()) {
                case "REGION" -> {
                    // 地区级：企业必须在地区管辖范围内（阿克苏地区全部县市）
                    return enterpriseArea == null || ALL_AREAS.contains(enterpriseArea)
                            || "阿克苏地区".equals(enterpriseArea);
                }
                case "CITY", "COUNTY" -> {
                    Set<Long> scopeIds = "CITY".equals(ctx.getRegionScope())
                            ? ctx.getCityIds() : ctx.getCountyIds();
                    boolean matched = scopeIds.stream().anyMatch(id -> {
                        OrgStructure org = orgStructureRepository.findById(id).orElse(null);
                        String area = org != null ? toAreaName(org.getName()) : null;
                        return area != null && area.equals(enterpriseArea);
                    });
                    if (matched) return true;
                }
            }
        }

        // 按科室判断
        if (ctx.getDeptScope() != null && !"ALL".equals(ctx.getDeptScope())) {
            if ("DEPT".equals(ctx.getDeptScope()) && enterpriseDeptId != null) {
                return ctx.getDeptIds().contains(enterpriseDeptId);
            }
        }

        return false;
    }

    /**
     * 获取用户可查看的区域列表（用于下拉框过滤/企业列表数据权限）。
     * 返回的是与企业 area 字段口径一致的短名。
     */
    public List<String> getAccessibleAreas(Long userId) {
        DataPermissionContext ctx = getDataPermission(userId);

        if ("ALL".equals(ctx.getDataScope()) || "ADMIN".equals(ctx.getDataScope())) {
            return ALL_AREAS;
        }
        // 地区级角色可见全部县市
        if ("REGION".equals(ctx.getRegionScope()) || "ALL".equals(ctx.getRegionScope())) {
            return ALL_AREAS;
        }

        Set<String> areas = new LinkedHashSet<>();
        for (Long cityId : ctx.getCityIds()) {
            orgStructureRepository.findById(cityId).ifPresent(city -> {
                String area = toAreaName(city.getName());
                if (area != null) areas.add(area);
            });
        }
        for (Long countyId : ctx.getCountyIds()) {
            orgStructureRepository.findById(countyId).ifPresent(county -> {
                String area = toAreaName(county.getName());
                if (area != null) areas.add(area);
            });
        }

        return new ArrayList<>(areas);
    }
}
