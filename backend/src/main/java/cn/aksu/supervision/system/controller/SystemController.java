package cn.aksu.supervision.system.controller;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.dto.EnterpriseDTO;
import cn.aksu.supervision.enterprise.entity.EnterpriseContact;
import cn.aksu.supervision.enterprise.service.EnterpriseService;
import cn.aksu.supervision.enterprise.repository.EnterpriseContactRepository;
import cn.aksu.supervision.system.dto.*;
import cn.aksu.supervision.system.entity.*;
import cn.aksu.supervision.system.repository.OrgStructureRepository;
import cn.aksu.supervision.system.service.SystemService;
import cn.aksu.supervision.system.service.DataPermissionService;
import cn.aksu.supervision.system.dto.DataPermissionContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "系统管理")
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class SystemController {

    private final SystemService systemService;
    private final OrgStructureRepository orgStructureRepository;
    private final EnterpriseService enterpriseService;
    private final EnterpriseContactRepository enterpriseContactRepository;
    private final DataPermissionService dataPermissionService;

    // ========== 角色 ==========

    @Operation(summary = "角色列表")
    @GetMapping("/role/list")
    public Result<PageResult<SysRole>> listRoles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listRoles(page, size));
    }

    @Operation(summary = "创建角色")
    @PostMapping("/role")
    public Result<SysRole> createRole(@Valid @RequestBody RoleRequest request) {
        return Result.success(systemService.createRole(request));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/role/{id}")
    public Result<SysRole> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return Result.success(systemService.updateRole(id, request));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/role/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        systemService.deleteRole(id);
        return Result.success();
    }

    // ========== 权限 ==========

    @Operation(summary = "权限列表")
    @GetMapping("/permission/list")
    public Result<PageResult<SysPermission>> listPermissions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listPermissions(page, size));
    }

    @Operation(summary = "创建权限")
    @PostMapping("/permission")
    public Result<SysPermission> createPermission(@Valid @RequestBody PermissionRequest request) {
        return Result.success(systemService.createPermission(request));
    }

    @Operation(summary = "更新权限")
    @PutMapping("/permission/{id}")
    public Result<SysPermission> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        return Result.success(systemService.updatePermission(id, request));
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/permission/{id}")
    public Result<Void> deletePermission(@PathVariable Long id) {
        systemService.deletePermission(id);
        return Result.success();
    }

    // ========== 用户 ==========

    @Operation(summary = "用户列表")
    @GetMapping("/user/list")
    public Result<PageResult<SysUser>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listUsers(page, size));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/user/{id}")
    public Result<SysUser> getUser(@PathVariable Long id) {
        return Result.success(systemService.getUser(id));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/user/{id}")
    public Result<SysUser> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return Result.success(systemService.updateUser(id, request));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/user/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        systemService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/user/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        systemService.resetPassword(id, newPassword);
        return Result.success();
    }

    // ========== 配置 ==========

    @Operation(summary = "配置列表")
    @GetMapping("/config/list")
    public Result<PageResult<SysConfig>> listConfigs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listConfigs(page, size));
    }

    @Operation(summary = "创建配置")
    @PostMapping("/config")
    public Result<SysConfig> createConfig(@RequestParam String configKey,
                                           @RequestParam String configValue,
                                           @RequestParam(required = false) String description,
                                           @RequestParam(required = false) String configGroup) {
        return Result.success(systemService.createConfig(configKey, configValue, description, configGroup));
    }

    @Operation(summary = "更新配置")
    @PutMapping("/config/{id}")
    public Result<SysConfig> updateConfig(@PathVariable Long id,
                                           @RequestParam String configValue,
                                           @RequestParam(required = false) String description) {
        return Result.success(systemService.updateConfig(id, configValue, description));
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/config/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        systemService.deleteConfig(id);
        return Result.success();
    }

    // ========== 操作日志 ==========

    @Operation(summary = "操作日志列表")
    @GetMapping("/log/list")
    public Result<PageResult<SysOperationLog>> listOperationLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listOperationLogs(username, operation, page, size));
    }

    // ========== 小程序用户管理 ==========

    @Operation(summary = "小程序用户列表")
    @GetMapping("/miniapp-user/list")
    public Result<Map<String, Object>> listMiniappUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long roleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listMiniappUsersWithEnterprise(keyword, area, industry, status, roleId, page, size));
    }

    @Operation(summary = "创建小程序用户")
    @PostMapping("/miniapp-user")
    public Result<SysUser> createMiniappUser(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        String realName = (String) request.get("realName");
        String phone = (String) request.get("phone");
        String userType = (String) request.getOrDefault("userType", "enterprise_user");
        Long roleId = request.get("roleId") != null ? ((Number) request.get("roleId")).longValue() : null;

        if (username == null || username.isBlank()) {
            return Result.error(400, "用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            return Result.error(400, "密码不能为空");
        }

        return Result.success(systemService.createMiniappUser(username, password, realName, phone, userType, roleId));
    }

    @Operation(summary = "小程序用户统计")
    @GetMapping("/miniapp-user/count")
    public Result<Map<String, Object>> miniappUserCount() {
        long count = systemService.countMiniappUsers();
        return Result.success(Map.of("total", count));
    }

    // ========== 后台管理-企业详情（执法人员视角） ==========

    @Operation(summary = "扫码查看企业详细信息（含联系人、整改、诉求）")
    @GetMapping("/enterprise/detail")
    public Result<Map<String, Object>> getEnterpriseDetailForInspector(
            @RequestParam String code) {
        EnterpriseDTO enterprise = enterpriseService.scanQuery(code);

        // 获取联系人列表
        List<EnterpriseContact> contacts = enterpriseContactRepository
                .findByEnterpriseIdOrderByIsPrimaryDescCreateTimeDesc(enterprise.getId());

        Map<String, Object> detail = new java.util.LinkedHashMap<>();
        detail.put("enterprise", enterprise);
        detail.put("contacts", contacts);

        return Result.success(detail);
    }

    @Operation(summary = "按企业ID查看企业详细信息")
    @GetMapping("/enterprise/{id}/detail")
    public Result<Map<String, Object>> getEnterpriseDetailById(@PathVariable Long id) {
        EnterpriseDTO enterprise = enterpriseService.getEnterpriseById(id);

        List<EnterpriseContact> contacts = enterpriseContactRepository
                .findByEnterpriseIdOrderByIsPrimaryDescCreateTimeDesc(id);

        Map<String, Object> detail = new java.util.LinkedHashMap<>();
        detail.put("enterprise", enterprise);
        detail.put("contacts", contacts);

        return Result.success(detail);
    }

    // ========== 组织架构管理 ==========

    @Operation(summary = "获取组织架构树")
    @GetMapping("/org/tree")
    public Result<List<OrgStructure>> getOrgTree() {
        return Result.success(systemService.getOrgTree());
    }

    @Operation(summary = "获取组织架构平铺列表")
    @GetMapping("/org/list")
    public Result<List<OrgStructure>> getOrgList() {
        return Result.success(systemService.getOrgList());
    }

    @Operation(summary = "创建组织（自动生成code和path）")
    @PostMapping("/org")
    public Result<OrgStructure> createOrg(@Valid @RequestBody OrgRequest request) {
        return Result.success(systemService.createOrgNodeWithPath(request));
    }

    @Operation(summary = "更新组织（支持父级变更重算path）")
    @PutMapping("/org/{id}")
    public Result<OrgStructure> updateOrg(@PathVariable Long id, @Valid @RequestBody OrgRequest request) {
        return Result.success(systemService.updateOrgNodeWithPath(id, request));
    }

    @Operation(summary = "获取组织完整路径链（从根到当前）")
    @GetMapping("/org/{id}/path")
    public Result<String> getOrgPath(@PathVariable Long id) {
        OrgStructure node = orgStructureRepository.findById(id)
                .orElseThrow(() -> new BusinessException("组织节点不存在"));
        return Result.success(node.getOrgPath());
    }

    @Operation(summary = "删除组织节点")
    @DeleteMapping("/org/{id}")
    public Result<Void> deleteOrgNode(@PathVariable Long id) {
        systemService.deleteOrgNode(id);
        return Result.success();
    }

    // ========== 岗位管理 ==========

    @Operation(summary = "岗位分页列表")
    @GetMapping("/position/list")
    public Result<PageResult<SysPosition>> listPositions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listPositions(page, size));
    }

    @Operation(summary = "创建岗位")
    @PostMapping("/position")
    public Result<SysPosition> createPosition(@Valid @RequestBody PositionRequest request) {
        return Result.success(systemService.createPosition(request));
    }

    @Operation(summary = "更新岗位")
    @PutMapping("/position/{id}")
    public Result<SysPosition> updatePosition(@PathVariable Long id, @Valid @RequestBody PositionRequest request) {
        return Result.success(systemService.updatePosition(id, request));
    }

    @Operation(summary = "删除岗位")
    @DeleteMapping("/position/{id}")
    public Result<Void> deletePosition(@PathVariable Long id) {
        systemService.deletePosition(id);
        return Result.success();
    }

    @Operation(summary = "按类别查询岗位")
    @GetMapping("/position/category/{category}")
    public Result<List<SysPosition>> getPositionsByCategory(@PathVariable String category) {
        return Result.success(systemService.getPositionsByCategory(category));
    }

    @Operation(summary = "获取所有岗位列表（不分页，供下拉选择）")
    @GetMapping("/position/all")
    public Result<List<SysPosition>> listAllPositions() {
        return Result.success(systemService.listAllPositions());
    }

    // ========== 职务管理 ==========

    @Operation(summary = "职务分页列表")
    @GetMapping("/job-title/list")
    public Result<PageResult<SysJobTitle>> listJobTitles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemService.listJobTitles(page, size));
    }

    @Operation(summary = "创建职务")
    @PostMapping("/job-title")
    public Result<SysJobTitle> createJobTitle(@Valid @RequestBody JobTitleRequest request) {
        return Result.success(systemService.createJobTitle(request));
    }

    @Operation(summary = "更新职务")
    @PutMapping("/job-title/{id}")
    public Result<SysJobTitle> updateJobTitle(@PathVariable Long id, @Valid @RequestBody JobTitleRequest request) {
        return Result.success(systemService.updateJobTitle(id, request));
    }

    @Operation(summary = "删除职务")
    @DeleteMapping("/job-title/{id}")
    public Result<Void> deleteJobTitle(@PathVariable Long id) {
        systemService.deleteJobTitle(id);
        return Result.success();
    }

    @Operation(summary = "查询领导职务")
    @GetMapping("/job-title/leadership")
    public Result<List<SysJobTitle>> getLeadershipJobTitles() {
        return Result.success(systemService.getLeadershipJobTitles());
    }

    @Operation(summary = "获取所有职务列表（不分页，供下拉选择）")
    @GetMapping("/job-title/all")
    public Result<List<SysJobTitle>> listAllJobTitles() {
        return Result.success(systemService.listAllJobTitles());
    }

    // ========== 用户岗位职务分配 ==========

    @Operation(summary = "分配用户岗位职务")
    @PostMapping("/user-position")
    public Result<OrgUserPosition> assignUserPosition(@Valid @RequestBody OrgUserPositionRequest request) {
        return Result.success(systemService.assignUserPosition(request));
    }

    @Operation(summary = "移除用户岗位职务分配")
    @DeleteMapping("/user-position/{id}")
    public Result<Void> removeUserPosition(@PathVariable Long id) {
        systemService.removeUserPosition(id);
        return Result.success();
    }

    @Operation(summary = "查询用户的岗位职务分配")
    @GetMapping("/user-position/user/{userId}")
    public Result<List<OrgUserPosition>> getUserPositions(@PathVariable Long userId) {
        return Result.success(systemService.getUserPositions(userId));
    }

    @Operation(summary = "查询组织的用户岗位职务分配")
    @GetMapping("/user-position/org/{orgId}")
    public Result<List<OrgUserPosition>> getOrgUserPositions(@PathVariable Long orgId) {
        return Result.success(systemService.getOrgUserPositions(orgId));
    }

    @Operation(summary = "设为主职")
    @PutMapping("/user-position/{id}/primary")
    public Result<Void> setPrimaryPosition(@PathVariable Long id) {
        systemService.setPrimaryPosition(id);
        return Result.success();
    }

    // ========== 事件驱动API ==========

    @Operation(summary = "用户入职")
    @PostMapping("/user/{userId}/onboard")
    public Result<Void> onboardUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        Long orgId = ((Number) request.get("orgId")).longValue();
        Long positionId = ((Number) request.get("positionId")).longValue();
        Long jobTitleId = ((Number) request.get("jobTitleId")).longValue();
        systemService.onboardUser(userId, orgId, positionId, jobTitleId);
        return Result.success();
    }

    @Operation(summary = "用户调动")
    @PostMapping("/user/{userId}/transfer")
    public Result<Void> transferUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        Long newOrgId = ((Number) request.get("newOrgId")).longValue();
        Long newPositionId = ((Number) request.get("newPositionId")).longValue();
        systemService.transferUser(userId, newOrgId, newPositionId);
        return Result.success();
    }

    @Operation(summary = "用户离职")
    @PostMapping("/user/{userId}/resign")
    public Result<Void> resignUser(@PathVariable Long userId) {
        systemService.resignUser(userId);
        return Result.success();
    }

    @Operation(summary = "用户升降级")
    @PostMapping("/user/{userId}/promote")
    public Result<Void> promoteUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        Long newJobTitleId = ((Number) request.get("newJobTitleId")).longValue();
        systemService.promoteUser(userId, newJobTitleId);
        return Result.success();
    }

    // ========== 企业行业分类 ==========

    @Operation(summary = "企业行业分类分页列表")
    @GetMapping("/enterprise-type/list")
    public Result<PageResult<EnterpriseType>> listEnterpriseTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(systemService.listEnterpriseTypes(page, size, keyword, category));
    }

    @Operation(summary = "创建企业行业分类")
    @PostMapping("/enterprise-type")
    public Result<EnterpriseType> createEnterpriseType(@RequestBody EnterpriseType request) {
        return Result.success(systemService.createEnterpriseType(request));
    }

    @Operation(summary = "更新企业行业分类")
    @PutMapping("/enterprise-type/{id}")
    public Result<EnterpriseType> updateEnterpriseType(@PathVariable Long id, @RequestBody EnterpriseType request) {
        return Result.success(systemService.updateEnterpriseType(id, request));
    }

    @Operation(summary = "删除企业行业分类")
    @DeleteMapping("/enterprise-type/{id}")
    public Result<Void> deleteEnterpriseType(@PathVariable Long id) {
        systemService.deleteEnterpriseType(id);
        return Result.success();
    }

    @Operation(summary = "获取启用的行业分类列表（不分页）")
    @GetMapping("/enterprise-type/active")
    public Result<List<EnterpriseType>> getActiveEnterpriseTypes() {
        return Result.success(systemService.getActiveEnterpriseTypes());
    }

    // ========== 角色权限详情管理 ==========

    @Operation(summary = "获取角色权限列表")
    @GetMapping("/role/{roleId}/permissions")
    public Result<List<RolePermissionDetail>> getRolePermissions(@PathVariable Long roleId) {
        return Result.success(systemService.getRolePermissions(roleId));
    }

    @Operation(summary = "更新角色权限")
    @PutMapping("/role/{roleId}/permissions")
    public Result<Void> updateRolePermissions(@PathVariable Long roleId,
                                               @RequestBody List<String> permissionCodes) {
        systemService.updateRolePermissions(roleId, permissionCodes);
        return Result.success();
    }

    @Operation(summary = "获取所有角色（含数据权限信息）")
    @GetMapping("/role/all")
    public Result<List<SysRole>> listAllRolesWithDataPermission() {
        return Result.success(systemService.listAllRolesWithDataPermission());
    }

    // ========== 用户权限分配 ==========

    @Operation(summary = "分配用户角色")
    @PutMapping("/user/{id}/role")
    public Result<SysUser> assignUserRole(@PathVariable Long id, @RequestParam Long roleId) {
        return Result.success(systemService.assignUserRole(id, roleId));
    }

    @Operation(summary = "分配用户数据权限范围")
    @PostMapping("/user/{id}/data-scope")
    public Result<Void> assignUserDataScope(@PathVariable Long id,
                                             @RequestBody List<Map<String, Object>> scopes) {
        systemService.assignUserDataScope(id, scopes);
        return Result.success();
    }

    @Operation(summary = "获取用户数据权限范围")
    @GetMapping("/user/{id}/data-scope")
    public Result<List<UserDataScope>> getUserDataScopes(@PathVariable Long id) {
        return Result.success(systemService.getUserDataScopes(id));
    }

    @Operation(summary = "检查用户是否有某权限")
    @GetMapping("/user/{id}/has-permission")
    public Result<Map<String, Object>> checkPermission(@PathVariable Long id,
                                                        @RequestParam String permissionCode) {
        boolean has = systemService.hasPermission(id, permissionCode);
        return Result.success(Map.of("hasPermission", has, "permissionCode", permissionCode));
    }

    @Operation(summary = "获取用户权限列表")
    @GetMapping("/user/{id}/permissions")
    public Result<List<String>> getUserPermissions(@PathVariable Long id) {
        return Result.success(systemService.getUserPermissions(id));
    }

    @Operation(summary = "获取用户数据权限上下文")
    @GetMapping("/user/{id}/data-permission-context")
    public Result<DataPermissionContext> getUserDataPermissionContext(@PathVariable Long id) {
        return Result.success(systemService.getUserDataPermission(id));
    }

    @Operation(summary = "获取用户可访问的区域列表")
    @GetMapping("/user/{id}/accessible-areas")
    public Result<List<String>> getAccessibleAreas(@PathVariable Long id) {
        return Result.success(dataPermissionService.getAccessibleAreas(id));
    }
}
