package cn.aksu.supervision.system.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.system.dto.*;
import cn.aksu.supervision.system.entity.*;
import cn.aksu.supervision.system.repository.*;
import cn.aksu.supervision.system.dto.*;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.system.service.DataPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemService {

    private final SysUserRepository sysUserRepository;
    private final SysRoleRepository sysRoleRepository;
    private final SysPermissionRepository sysPermissionRepository;
    private final SysConfigRepository sysConfigRepository;
    private final SysOperationLogRepository sysOperationLogRepository;
    private final OrgStructureRepository orgStructureRepository;
    private final SysPositionRepository sysPositionRepository;
    private final SysJobTitleRepository sysJobTitleRepository;
    private final OrgUserPositionRepository orgUserPositionRepository;
    private final EnterpriseTypeRepository enterpriseTypeRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataPermissionService dataPermissionService;
    private final RolePermissionDetailRepository rolePermissionDetailRepository;
    private final UserDataScopeRepository userDataScopeRepository;

    // ========== 角色 CRUD ==========

    @Transactional(readOnly = true)
    public PageResult<SysRole> listRoles(int page, int size) {
        Page<SysRole> pageData = sysRoleRepository.findAll(PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public SysRole createRole(RoleRequest request) {
        if (sysRoleRepository.findByRoleCode(request.getRoleCode()).isPresent()) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole role = SysRole.builder()
                .roleCode(request.getRoleCode())
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .dataScope(request.getDataScope() != null ? request.getDataScope() : "SELF")
                .regionScope(request.getRegionScope() != null ? request.getRegionScope() : "SELF")
                .deptScope(request.getDeptScope() != null ? request.getDeptScope() : "SELF")
                .orgLevel(request.getOrgLevel())
                .parentId(request.getParentId())
                .roleType(request.getRoleType() != null ? request.getRoleType() : "BUSINESS")
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(1)
                .build();
        return sysRoleRepository.save(role);
    }

    public SysRole updateRole(Long id, RoleRequest request) {
        SysRole role = sysRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        if (request.getDataScope() != null) role.setDataScope(request.getDataScope());
        if (request.getRegionScope() != null) role.setRegionScope(request.getRegionScope());
        if (request.getDeptScope() != null) role.setDeptScope(request.getDeptScope());
        if (request.getOrgLevel() != null) role.setOrgLevel(request.getOrgLevel());
        if (request.getParentId() != null) role.setParentId(request.getParentId());
        if (request.getRoleType() != null) role.setRoleType(request.getRoleType());
        if (request.getSortOrder() != null) role.setSortOrder(request.getSortOrder());
        if (request.getStatus() != null) role.setStatus(request.getStatus());
        return sysRoleRepository.save(role);
    }

    public void deleteRole(Long id) {
        sysRoleRepository.deleteById(id);
    }

    // ========== 权限 CRUD ==========

    @Transactional(readOnly = true)
    public PageResult<SysPermission> listPermissions(int page, int size) {
        Page<SysPermission> pageData = sysPermissionRepository.findAll(PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public SysPermission createPermission(PermissionRequest request) {
        if (sysPermissionRepository.findByPermissionCode(request.getPermissionCode()).isPresent()) {
            throw new BusinessException("权限编码已存在");
        }
        SysPermission permission = SysPermission.builder()
                .permissionCode(request.getPermissionCode())
                .permissionName(request.getPermissionName())
                .permissionType(request.getPermissionType())
                .module(request.getModule())
                .url(request.getUrl())
                .method(request.getMethod())
                .parentId(request.getParentId())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(1)
                .build();
        return sysPermissionRepository.save(permission);
    }

    public SysPermission updatePermission(Long id, PermissionRequest request) {
        SysPermission permission = sysPermissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionType(request.getPermissionType());
        permission.setModule(request.getModule());
        permission.setUrl(request.getUrl());
        permission.setMethod(request.getMethod());
        permission.setParentId(request.getParentId());
        permission.setSortOrder(request.getSortOrder());
        return sysPermissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        sysPermissionRepository.deleteById(id);
    }

    // ========== 用户 CRUD ==========

    @Transactional(readOnly = true)
    public PageResult<SysUser> listUsers(int page, int size) {
        Page<SysUser> pageData = sysUserRepository.findAll(PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public SysUser updateUser(Long id, UserUpdateRequest request) {
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (request.getRealName() != null) user.setRealName(request.getRealName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getUserType() != null) user.setUserType(request.getUserType());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getRoleId() != null) user.setRoleId(request.getRoleId());

        // 组织架构
        if (request.getOrgId() != null) {
            user.setOrgId(request.getOrgId());
            // 根据orgId自动填充orgName
            orgStructureRepository.findById(request.getOrgId())
                    .ifPresent(org -> user.setOrgName(org.getName()));
        }
        if (request.getOrgName() != null) user.setOrgName(request.getOrgName());

        // 岗位
        if (request.getPositionId() != null) {
            user.setPositionId(request.getPositionId());
            // 根据positionId自动填充positionName
            sysPositionRepository.findById(request.getPositionId())
                    .ifPresent(pos -> user.setPositionName(pos.getPositionName()));
        }
        if (request.getPositionName() != null) user.setPositionName(request.getPositionName());

        // 职务
        if (request.getJobTitleId() != null) {
            user.setJobTitleId(request.getJobTitleId());
            // 根据jobTitleId自动填充jobTitleName
            sysJobTitleRepository.findById(request.getJobTitleId())
                    .ifPresent(jt -> user.setJobTitleName(jt.getTitleName()));
        }
        if (request.getJobTitleName() != null) user.setJobTitleName(request.getJobTitleName());

        return sysUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        sysUserRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SysUser getUser(Long id) {
        return sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    public void resetPassword(Long id, String newPassword) {
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserRepository.save(user);
    }

    // ========== 配置 CRUD ==========

    @Transactional(readOnly = true)
    public PageResult<SysConfig> listConfigs(int page, int size) {
        Page<SysConfig> pageData = sysConfigRepository.findAll(PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public SysConfig createConfig(String configKey, String configValue, String description, String configGroup) {
        if (sysConfigRepository.findByConfigKey(configKey).isPresent()) {
            throw new BusinessException("配置键已存在");
        }
        SysConfig config = SysConfig.builder()
                .configKey(configKey)
                .configValue(configValue)
                .description(description)
                .configGroup(configGroup)
                .build();
        return sysConfigRepository.save(config);
    }

    public SysConfig updateConfig(Long id, String configValue, String description) {
        SysConfig config = sysConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("配置不存在"));
        config.setConfigValue(configValue);
        config.setDescription(description);
        return sysConfigRepository.save(config);
    }

    public void deleteConfig(Long id) {
        sysConfigRepository.deleteById(id);
    }

    // ========== 操作日志 ==========

    @Transactional(readOnly = true)
    public PageResult<SysOperationLog> listOperationLogs(String username, String operation, int page, int size) {
        Page<SysOperationLog> pageData = sysOperationLogRepository.findByConditions(username, operation,
                PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    // ========== 小程序用户管理 ==========

    /**
     * 查询小程序端用户列表（企业用户 enterprise_user），含企业关联信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> listMiniappUsersWithEnterprise(String keyword, String area, String industry, Integer status, Long roleId, int page, int size) {
        // 如果有行业或地区筛选，优先从Enterprise表反查userId
        if ((area != null && !area.isBlank()) || (industry != null && !industry.isBlank())) {
            return listByEnterpriseFilter(keyword, area, industry, status, roleId, page, size);
        }

        Page<SysUser> pageData;
        List<String> userTypes = List.of("enterprise_user", "enterprise");

        if (roleId != null && keyword != null && !keyword.isBlank()) {
            pageData = sysUserRepository.findByUserTypeInAndRoleIdAndKeyword(
                    userTypes, roleId, keyword, PageRequest.of(page - 1, size));
        } else if (roleId != null && status != null) {
            pageData = sysUserRepository.findByUserTypeInAndRoleIdAndStatus(
                    userTypes, roleId, status, PageRequest.of(page - 1, size));
        } else if (roleId != null) {
            pageData = sysUserRepository.findByUserTypeInAndRoleId(
                    userTypes, roleId, PageRequest.of(page - 1, size));
        } else if (keyword != null && !keyword.isBlank()) {
            pageData = sysUserRepository.findByUserTypeInAndKeyword(
                    userTypes, keyword, PageRequest.of(page - 1, size));
        } else if (status != null) {
            pageData = sysUserRepository.findByUserTypeInAndStatus(
                    userTypes, status, PageRequest.of(page - 1, size));
        } else {
            pageData = sysUserRepository.findByUserTypeIn(
                    userTypes, PageRequest.of(page - 1, size));
        }

        List<Map<String, Object>> list = enrichWithEnterprise(pageData.getContent());
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", pageData.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    /**
     * 通过Enterprise表筛选条件反查用户
     */
    private Map<String, Object> listByEnterpriseFilter(String keyword, String area, String industry, Integer status, Long roleId, int page, int size) {
        // 查找符合条件的Enterprise的userId列表
        List<Enterprise> enterprises = enterpriseRepository.findAll().stream()
                .filter(e -> (area == null || area.isBlank() || area.equals(e.getArea()))
                          && (industry == null || industry.isBlank() || industry.equals(e.getIndustry())))
                .toList();

        List<Long> userIds = enterprises.stream()
                .map(Enterprise::getUserId)
                .filter(uid -> uid != null)
                .distinct()
                .toList();

        if (userIds.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("list", List.of());
            result.put("total", 0L);
            result.put("page", page);
            result.put("size", size);
            return result;
        }

        Page<SysUser> pageData;
        if (roleId != null && keyword != null && !keyword.isBlank()) {
            pageData = sysUserRepository.findByIdInAndRoleIdAndKeyword(userIds, roleId, keyword, PageRequest.of(page - 1, size));
        } else if (roleId != null && status != null) {
            pageData = sysUserRepository.findByIdInAndRoleIdAndStatus(userIds, roleId, status, PageRequest.of(page - 1, size));
        } else if (roleId != null) {
            pageData = sysUserRepository.findByIdInAndRoleId(userIds, roleId, PageRequest.of(page - 1, size));
        } else if (keyword != null && !keyword.isBlank()) {
            pageData = sysUserRepository.findByIdInAndKeyword(userIds, keyword, PageRequest.of(page - 1, size));
        } else if (status != null) {
            pageData = sysUserRepository.findByIdInAndStatus(userIds, status, PageRequest.of(page - 1, size));
        } else {
            pageData = sysUserRepository.findByIdIn(userIds, PageRequest.of(page - 1, size));
        }

        List<Map<String, Object>> list = enrichWithEnterprise(pageData.getContent());
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", pageData.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    /**
     * 为用户列表附加企业关联信息
     */
    private List<Map<String, Object>> enrichWithEnterprise(List<SysUser> users) {
        // 批量查询关联的Enterprise
        List<Long> userIds = users.stream().map(SysUser::getId).toList();
        Map<Long, Enterprise> enterpriseMap = new HashMap<>();
        for (Long uid : userIds) {
            enterpriseRepository.findByUserId(uid).ifPresent(e -> enterpriseMap.put(uid, e));
        }

        return users.stream().map(user -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", user.getId());
            item.put("username", user.getUsername());
            item.put("realName", user.getRealName());
            item.put("phone", user.getPhone());
            item.put("userType", user.getUserType());
            item.put("status", user.getStatus());
            item.put("createTime", user.getCreateTime());
            item.put("roleId", user.getRoleId());

            Enterprise ent = enterpriseMap.get(user.getId());
            if (ent != null) {
                item.put("enterpriseId", ent.getId());
                item.put("enterpriseName", ent.getName());
                item.put("area", ent.getArea());
                item.put("industry", ent.getIndustry());
            } else {
                item.put("enterpriseId", null);
                item.put("enterpriseName", null);
                item.put("area", null);
                item.put("industry", null);
            }
            return item;
        }).toList();
    }

    /**
     * 创建小程序用户
     */
    public SysUser createMiniappUser(String username, String password, String realName, String phone, String userType, Long roleId) {
        if (sysUserRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }
        if (phone != null && !phone.isBlank() && sysUserRepository.existsByPhone(phone)) {
            throw new BusinessException("手机号已注册");
        }
        SysUser user = SysUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .realName(realName)
                .phone(phone)
                .userType(userType != null ? userType : "enterprise_user")
                .roleId(roleId)
                .status(1)
                .build();
        return sysUserRepository.save(user);
    }

    /**
     * 统计小程序用户数
     */
    @Transactional(readOnly = true)
    public long countMiniappUsers() {
        return sysUserRepository.countByUserType("enterprise_user")
                + sysUserRepository.countByUserType("enterprise");
    }

    // ========== 组织架构管理 ==========

    /**
     * 获取组织架构树
     */
    @Transactional(readOnly = true)
    public List<OrgStructure> getOrgTree() {
        List<OrgStructure> all = orgStructureRepository.findAll();
        return buildTree(all);
    }

    /**
     * 获取组织架构平铺列表
     */
    @Transactional(readOnly = true)
    public List<OrgStructure> getOrgList() {
        return orgStructureRepository.findAll();
    }

    /**
     * 创建组织节点
     */
    public OrgStructure createOrgNode(OrgStructure node) {
        // 校验层级：parent的level必须比当前节点小1
        if (node.getParentId() != null) {
            OrgStructure parent = orgStructureRepository.findById(node.getParentId())
                    .orElseThrow(() -> new BusinessException("父级组织不存在"));
            if (parent.getLevel() != node.getLevel() - 1) {
                throw new BusinessException("层级关系不正确：父级层级应为" + (node.getLevel() - 1));
            }
        } else {
            // 无父节点，必须是第1级（地区）
            if (node.getLevel() != 1) {
                throw new BusinessException("顶级节点必须是地区级别（level=1）");
            }
        }
        if (node.getLevel() < 1 || node.getLevel() > 5) {
            throw new BusinessException("层级必须在1-5之间");
        }
        return orgStructureRepository.save(node);
    }

    /**
     * 更新组织节点
     */
    public OrgStructure updateOrgNode(Long id, OrgStructure update) {
        OrgStructure existing = orgStructureRepository.findById(id)
                .orElseThrow(() -> new BusinessException("组织节点不存在"));
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getSortOrder() != null) existing.setSortOrder(update.getSortOrder());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        if (update.getDescription() != null) existing.setDescription(update.getDescription());
        return orgStructureRepository.save(existing);
    }

    /**
     * 删除组织节点（级联删除子节点）
     */
    public void deleteOrgNode(Long id) {
        OrgStructure node = orgStructureRepository.findById(id)
                .orElseThrow(() -> new BusinessException("组织节点不存在"));
        // 递归删除所有子节点
        deleteChildren(id);
        orgStructureRepository.delete(node);
    }

    private void deleteChildren(Long parentId) {
        List<OrgStructure> children = orgStructureRepository.findByParentIdOrderBySortOrder(parentId);
        for (OrgStructure child : children) {
            deleteChildren(child.getId());
            orgStructureRepository.delete(child);
        }
    }

    /**
     * 构建树形结构
     */
    private List<OrgStructure> buildTree(List<OrgStructure> all) {
        Map<Long, List<OrgStructure>> childrenMap = all.stream()
                .filter(o -> o.getParentId() != null)
                .collect(Collectors.groupingBy(OrgStructure::getParentId));

        List<OrgStructure> roots = all.stream()
                .filter(o -> o.getParentId() == null)
                .sorted((a, b) -> Integer.compare(
                        a.getSortOrder() != null ? a.getSortOrder() : 0,
                        b.getSortOrder() != null ? b.getSortOrder() : 0))
                .collect(Collectors.toList());

        for (OrgStructure root : roots) {
            root.setChildren(getChildren(root.getId(), childrenMap));
        }
        return roots;
    }

    private List<OrgStructure> getChildren(Long parentId, Map<Long, List<OrgStructure>> childrenMap) {
        List<OrgStructure> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        children.sort((a, b) -> Integer.compare(
                a.getSortOrder() != null ? a.getSortOrder() : 0,
                b.getSortOrder() != null ? b.getSortOrder() : 0));
        for (OrgStructure child : children) {
            child.setChildren(getChildren(child.getId(), childrenMap));
        }
        return children;
    }

    // ========== Path生成与维护 ==========

    /**
     * 生成组织编码，格式为 ORG-{父编码}-{自增序号}，如 ORG-AKS-001
     */
    public String generateOrgCode(Long parentId, Integer level) {
        String prefix = "ORG";
        if (parentId != null) {
            OrgStructure parent = orgStructureRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException("父级组织不存在"));
            if (parent.getOrgCode() != null && !parent.getOrgCode().isBlank()) {
                prefix = parent.getOrgCode();
            }
        }
        long count;
        if (parentId != null) {
            count = orgStructureRepository.countByParentId(parentId);
        } else {
            count = orgStructureRepository.findByParentIdIsNullOrderBySortOrder().size();
        }
        String seq = String.format("%03d", count + 1);
        return prefix + "-" + seq;
    }

    /**
     * 生成路径，拼接父path + currentId + "/"
     */
    public String generateOrgPath(Long parentId, Long currentId) {
        if (parentId == null) {
            return "/" + currentId + "/";
        }
        OrgStructure parent = orgStructureRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException("父级组织不存在"));
        String parentPath = parent.getOrgPath();
        if (parentPath == null || parentPath.isBlank()) {
            parentPath = "/";
        }
        return parentPath + currentId + "/";
    }

    /**
     * 级联更新子节点的path
     */
    public void updateChildPaths(Long parentId, String parentPath) {
        List<OrgStructure> children = orgStructureRepository.findByParentIdOrderBySortOrder(parentId);
        for (OrgStructure child : children) {
            String newPath = parentPath + child.getId() + "/";
            child.setOrgPath(newPath);
            orgStructureRepository.save(child);
            updateChildPaths(child.getId(), newPath);
        }
    }

    // ========== 组织架构增强CRUD ==========

    /**
     * 创建节点时自动生成orgCode和orgPath
     */
    public OrgStructure createOrgNodeWithPath(OrgRequest request) {
        // 校验层级
        if (request.getParentId() != null) {
            OrgStructure parent = orgStructureRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父级组织不存在"));
            if (parent.getLevel() >= request.getLevel()) {
                throw new BusinessException("层级关系不正确：父级层级必须小于当前层级");
            }
        } else {
            if (request.getLevel() != 1) {
                throw new BusinessException("顶级节点必须是地区级别（level=1）");
            }
        }
        if (request.getLevel() < 1 || request.getLevel() > 5) {
            throw new BusinessException("层级必须在1-5之间");
        }

        OrgStructure node = OrgStructure.builder()
                .name(request.getName())
                .parentId(request.getParentId())
                .level(request.getLevel())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .description(request.getDescription())
                .orgType(request.getOrgType())
                .unifiedSocialCreditCode(request.getUnifiedSocialCreditCode())
                .leaderName(request.getLeaderName())
                .leaderPhone(request.getLeaderPhone())
                .address(request.getAddress())
                .remark(request.getRemark())
                .build();

        // 先保存以获取ID
        OrgStructure saved = orgStructureRepository.save(node);

        // 生成orgCode和orgPath
        saved.setOrgCode(generateOrgCode(saved.getParentId(), saved.getLevel()));
        saved.setOrgPath(generateOrgPath(saved.getParentId(), saved.getId()));

        return orgStructureRepository.save(saved);
    }

    /**
     * 更新节点，如果父级改变则重新计算path
     */
    public OrgStructure updateOrgNodeWithPath(Long id, OrgRequest request) {
        OrgStructure existing = orgStructureRepository.findById(id)
                .orElseThrow(() -> new BusinessException("组织节点不存在"));

        Long oldParentId = existing.getParentId();

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getSortOrder() != null) existing.setSortOrder(request.getSortOrder());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getOrgType() != null) existing.setOrgType(request.getOrgType());
        if (request.getUnifiedSocialCreditCode() != null)
            existing.setUnifiedSocialCreditCode(request.getUnifiedSocialCreditCode());
        if (request.getLeaderName() != null) existing.setLeaderName(request.getLeaderName());
        if (request.getLeaderPhone() != null) existing.setLeaderPhone(request.getLeaderPhone());
        if (request.getAddress() != null) existing.setAddress(request.getAddress());
        if (request.getRemark() != null) existing.setRemark(request.getRemark());

        // 如果父级改变，重新计算path
        if (request.getParentId() != null && !request.getParentId().equals(oldParentId)) {
            // 校验新父级
            OrgStructure newParent = orgStructureRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("新父级组织不存在"));
            if (newParent.getLevel() >= existing.getLevel()) {
                throw new BusinessException("层级关系不正确：父级层级必须小于当前层级");
            }
            // 检查是否将自己设为自己的父级或后代
            if (request.getParentId().equals(id)) {
                throw new BusinessException("不能将自己设为自己的父级");
            }

            existing.setParentId(request.getParentId());
            existing.setOrgPath(generateOrgPath(existing.getParentId(), existing.getId()));
            existing.setOrgCode(generateOrgCode(existing.getParentId(), existing.getLevel()));

            OrgStructure updated = orgStructureRepository.save(existing);
            // 级联更新子节点path
            updateChildPaths(updated.getId(), updated.getOrgPath());
            return updated;
        }

        return orgStructureRepository.save(existing);
    }

    // ========== 岗位管理CRUD ==========

    @Transactional(readOnly = true)
    public PageResult<SysPosition> listPositions(int page, int size) {
        Page<SysPosition> pageData = sysPositionRepository.findAll(PageRequest.of(page - 1, size));
        // 填充角色名称
        for (SysPosition pos : pageData.getContent()) {
            if (pos.getRoleId() != null) {
                sysRoleRepository.findById(pos.getRoleId()).ifPresent(role -> pos.setRoleName(role.getRoleName()));
            }
            if (pos.getOrgId() != null) {
                orgStructureRepository.findById(pos.getOrgId()).ifPresent(org -> pos.setOrgName(org.getName()));
            }
        }
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public SysPosition createPosition(PositionRequest request) {
        if (sysPositionRepository.existsByPositionCode(request.getPositionCode())) {
            throw new BusinessException("岗位编码已存在");
        }
        SysPosition position = SysPosition.builder()
                .positionCode(request.getPositionCode())
                .positionName(request.getPositionName())
                .category(request.getCategory())
                .salaryRangeMin(request.getSalaryRangeMin())
                .salaryRangeMax(request.getSalaryRangeMax())
                .orgId(request.getOrgId())
                .level(request.getLevel() != null ? request.getLevel() : 1)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .roleId(request.getRoleId())
                .description(request.getDescription())
                .build();
        return sysPositionRepository.save(position);
    }

    public SysPosition updatePosition(Long id, PositionRequest request) {
        SysPosition position = sysPositionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("岗位不存在"));
        if (request.getPositionCode() != null) {
            if (!position.getPositionCode().equals(request.getPositionCode())
                    && sysPositionRepository.existsByPositionCode(request.getPositionCode())) {
                throw new BusinessException("岗位编码已存在");
            }
            position.setPositionCode(request.getPositionCode());
        }
        if (request.getPositionName() != null) position.setPositionName(request.getPositionName());
        if (request.getCategory() != null) position.setCategory(request.getCategory());
        if (request.getSalaryRangeMin() != null) position.setSalaryRangeMin(request.getSalaryRangeMin());
        if (request.getSalaryRangeMax() != null) position.setSalaryRangeMax(request.getSalaryRangeMax());
        if (request.getOrgId() != null) position.setOrgId(request.getOrgId());
        if (request.getLevel() != null) position.setLevel(request.getLevel());
        if (request.getStatus() != null) position.setStatus(request.getStatus());
        if (request.getSortOrder() != null) position.setSortOrder(request.getSortOrder());
        if (request.getRoleId() != null) position.setRoleId(request.getRoleId());
        if (request.getDescription() != null) position.setDescription(request.getDescription());
        return sysPositionRepository.save(position);
    }

    public void deletePosition(Long id) {
        sysPositionRepository.deleteById(id);
    }

    // ========== 职务管理CRUD ==========

    @Transactional(readOnly = true)
    public PageResult<SysJobTitle> listJobTitles(int page, int size) {
        Page<SysJobTitle> pageData = sysJobTitleRepository.findAll(PageRequest.of(page - 1, size));
        // 填充角色名称
        for (SysJobTitle jt : pageData.getContent()) {
            if (jt.getRoleId() != null) {
                sysRoleRepository.findById(jt.getRoleId()).ifPresent(role -> jt.setRoleName(role.getRoleName()));
            }
        }
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public SysJobTitle createJobTitle(JobTitleRequest request) {
        if (sysJobTitleRepository.existsByTitleCode(request.getTitleCode())) {
            throw new BusinessException("职务编码已存在");
        }
        SysJobTitle jobTitle = SysJobTitle.builder()
                .titleCode(request.getTitleCode())
                .titleName(request.getTitleName())
                .category(request.getCategory())
                .isLeadership(request.getIsLeadership() != null ? request.getIsLeadership() : 0)
                .level(request.getLevel() != null ? request.getLevel() : 1)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .roleId(request.getRoleId())
                .description(request.getDescription())
                .build();
        return sysJobTitleRepository.save(jobTitle);
    }

    public SysJobTitle updateJobTitle(Long id, JobTitleRequest request) {
        SysJobTitle jobTitle = sysJobTitleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("职务不存在"));
        if (request.getTitleCode() != null) {
            if (!jobTitle.getTitleCode().equals(request.getTitleCode())
                    && sysJobTitleRepository.existsByTitleCode(request.getTitleCode())) {
                throw new BusinessException("职务编码已存在");
            }
            jobTitle.setTitleCode(request.getTitleCode());
        }
        if (request.getTitleName() != null) jobTitle.setTitleName(request.getTitleName());
        if (request.getCategory() != null) jobTitle.setCategory(request.getCategory());
        if (request.getIsLeadership() != null) jobTitle.setIsLeadership(request.getIsLeadership());
        if (request.getLevel() != null) jobTitle.setLevel(request.getLevel());
        if (request.getStatus() != null) jobTitle.setStatus(request.getStatus());
        if (request.getSortOrder() != null) jobTitle.setSortOrder(request.getSortOrder());
        if (request.getRoleId() != null) jobTitle.setRoleId(request.getRoleId());
        if (request.getDescription() != null) jobTitle.setDescription(request.getDescription());
        return sysJobTitleRepository.save(jobTitle);
    }

    public void deleteJobTitle(Long id) {
        sysJobTitleRepository.deleteById(id);
    }

    // ========== 用户岗位职务分配 ==========

    /**
     * 分配用户到组织+岗位+职务
     */
    public OrgUserPosition assignUserPosition(OrgUserPositionRequest request) {
        // 检查用户是否已存在该组织的分配
        Optional<OrgUserPosition> existing = orgUserPositionRepository
                .findByUserIdAndOrgId(request.getUserId(), request.getOrgId());
        if (existing.isPresent()) {
            throw new BusinessException("用户在该组织已存在分配记录");
        }

        // 校验用户存在
        sysUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        // 校验组织存在
        orgStructureRepository.findById(request.getOrgId())
                .orElseThrow(() -> new BusinessException("组织不存在"));
        // 校验岗位存在
        if (request.getPositionId() != null) {
            sysPositionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new BusinessException("岗位不存在"));
        }
        // 校验职务存在
        if (request.getJobTitleId() != null) {
            sysJobTitleRepository.findById(request.getJobTitleId())
                    .orElseThrow(() -> new BusinessException("职务不存在"));
        }

        OrgUserPosition assignment = OrgUserPosition.builder()
                .userId(request.getUserId())
                .orgId(request.getOrgId())
                .positionId(request.getPositionId())
                .jobTitleId(request.getJobTitleId())
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : 1)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .build();
        return orgUserPositionRepository.save(assignment);
    }

    /**
     * 移除分配
     */
    public void removeUserPosition(Long id) {
        OrgUserPosition assignment = orgUserPositionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分配记录不存在"));
        orgUserPositionRepository.delete(assignment);
    }

    /**
     * 查询用户的分配
     */
    @Transactional(readOnly = true)
    public List<OrgUserPosition> listUserPositions(Long userId) {
        List<OrgUserPosition> list = orgUserPositionRepository.findByUserId(userId);
        // 填充关联名称
        for (OrgUserPosition item : list) {
            orgStructureRepository.findById(item.getOrgId()).ifPresent(org -> item.setOrgName(org.getName()));
            if (item.getPositionId() != null) {
                sysPositionRepository.findById(item.getPositionId())
                        .ifPresent(pos -> item.setPositionName(pos.getPositionName()));
            }
            if (item.getJobTitleId() != null) {
                sysJobTitleRepository.findById(item.getJobTitleId())
                        .ifPresent(jt -> item.setJobTitleName(jt.getTitleName()));
            }
            sysUserRepository.findById(item.getUserId()).ifPresent(user -> {
                item.setUsername(user.getUsername());
                item.setRealName(user.getRealName());
            });
        }
        return list;
    }

    /**
     * 查询组织的用户
     */
    @Transactional(readOnly = true)
    public List<OrgUserPosition> listOrgUsers(Long orgId) {
        List<OrgUserPosition> list = orgUserPositionRepository.findByOrgIdAndStatus(orgId, 1);
        for (OrgUserPosition item : list) {
            orgStructureRepository.findById(item.getOrgId()).ifPresent(org -> item.setOrgName(org.getName()));
            if (item.getPositionId() != null) {
                sysPositionRepository.findById(item.getPositionId())
                        .ifPresent(pos -> item.setPositionName(pos.getPositionName()));
            }
            if (item.getJobTitleId() != null) {
                sysJobTitleRepository.findById(item.getJobTitleId())
                        .ifPresent(jt -> item.setJobTitleName(jt.getTitleName()));
            }
            sysUserRepository.findById(item.getUserId()).ifPresent(user -> {
                item.setUsername(user.getUsername());
                item.setRealName(user.getRealName());
            });
        }
        return list;
    }

    // ========== 事件驱动权限变更 ==========

    /**
     * 入职：为用户分配组织+岗位+职务
     */
    public void handleUserOnboard(Long userId, Long orgId, Long positionId, Long jobTitleId) {
        // 校验用户
        sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 创建分配记录
        OrgUserPositionRequest request = new OrgUserPositionRequest();
        request.setUserId(userId);
        request.setOrgId(orgId);
        request.setPositionId(positionId);
        request.setJobTitleId(jobTitleId);
        request.setIsPrimary(1);
        request.setStartDate(LocalDate.now());
        request.setStatus(1);

        assignUserPosition(request);
    }

    /**
     * 调动：变更用户的组织和岗位
     */
    public void handleUserTransfer(Long userId, Long newOrgId, Long newPositionId) {
        // 查找用户当前的主职分配
        Optional<OrgUserPosition> primaryOpt = orgUserPositionRepository.findPrimaryByUserId(userId);
        if (primaryOpt.isPresent()) {
            OrgUserPosition primary = primaryOpt.get();
            primary.setStatus(0); // 设为无效
            primary.setEndDate(LocalDate.now());
            orgUserPositionRepository.save(primary);
        }

        // 创建新的分配
        OrgUserPositionRequest request = new OrgUserPositionRequest();
        request.setUserId(userId);
        request.setOrgId(newOrgId);
        request.setPositionId(newPositionId);
        request.setIsPrimary(1);
        request.setStartDate(LocalDate.now());
        request.setStatus(1);

        assignUserPosition(request);
    }

    /**
     * 离职：停用用户所有分配
     */
    public void handleUserResign(Long userId) {
        List<OrgUserPosition> assignments = orgUserPositionRepository.findByUserId(userId);
        for (OrgUserPosition assignment : assignments) {
            assignment.setStatus(0); // 离职
            assignment.setEndDate(LocalDate.now());
            orgUserPositionRepository.save(assignment);
        }

        // 停用用户账号
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setStatus(0);
        sysUserRepository.save(user);
    }

    /**
     * 升降级：变更用户的职务
     */
    public void handleUserPromote(Long userId, Long newJobTitleId) {
        // 查找用户当前的主职分配
        OrgUserPosition primary = orgUserPositionRepository.findPrimaryByUserId(userId)
                .orElseThrow(() -> new BusinessException("用户没有主职分配记录"));

        // 校验新职务
        sysJobTitleRepository.findById(newJobTitleId)
                .orElseThrow(() -> new BusinessException("职务不存在"));

        primary.setJobTitleId(newJobTitleId);
        orgUserPositionRepository.save(primary);
    }

    // ========== 补充方法（Controller需要） ==========

    /**
     * 按类别查询岗位
     */
    @Transactional(readOnly = true)
    public List<SysPosition> getPositionsByCategory(String category) {
        return sysPositionRepository.findByCategoryAndStatusOrderBySortOrder(category, 1);
    }

    /**
     * 获取所有岗位列表（不分页，供下拉选择）
     */
    @Transactional(readOnly = true)
    public List<SysPosition> listAllPositions() {
        List<SysPosition> list = sysPositionRepository.findByStatusOrderBySortOrder(1);
        for (SysPosition pos : list) {
            if (pos.getRoleId() != null) {
                sysRoleRepository.findById(pos.getRoleId()).ifPresent(role -> pos.setRoleName(role.getRoleName()));
            }
            if (pos.getOrgId() != null) {
                orgStructureRepository.findById(pos.getOrgId()).ifPresent(org -> pos.setOrgName(org.getName()));
            }
        }
        return list;
    }

    /**
     * 查询领导职务
     */
    @Transactional(readOnly = true)
    public List<SysJobTitle> getLeadershipJobTitles() {
        return sysJobTitleRepository.findByIsLeadershipAndStatusOrderByLevel(1, 1);
    }

    /**
     * 获取所有职务列表（不分页，供下拉选择）
     */
    @Transactional(readOnly = true)
    public List<SysJobTitle> listAllJobTitles() {
        List<SysJobTitle> list = sysJobTitleRepository.findByStatusOrderBySortOrder(1);
        for (SysJobTitle jt : list) {
            if (jt.getRoleId() != null) {
                sysRoleRepository.findById(jt.getRoleId()).ifPresent(role -> jt.setRoleName(role.getRoleName()));
            }
        }
        return list;
    }

    /**
     * 查询用户的岗位职务分配（Controller别名）
     */
    @Transactional(readOnly = true)
    public List<OrgUserPosition> getUserPositions(Long userId) {
        return listUserPositions(userId);
    }

    /**
     * 查询组织的用户分配（Controller别名）
     */
    @Transactional(readOnly = true)
    public List<OrgUserPosition> getOrgUserPositions(Long orgId) {
        return listOrgUsers(orgId);
    }

    /**
     * 设为主职
     */
    public void setPrimaryPosition(Long id) {
        OrgUserPosition target = orgUserPositionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分配记录不存在"));
        Long userId = target.getUserId();
        // 将该用户所有分配设为非主职
        List<OrgUserPosition> all = orgUserPositionRepository.findByUserId(userId);
        for (OrgUserPosition item : all) {
            item.setIsPrimary(item.getId().equals(id) ? 1 : 0);
            orgUserPositionRepository.save(item);
        }
    }

    /**
     * 用户入职（Controller别名）
     */
    public void onboardUser(Long userId, Long orgId, Long positionId, Long jobTitleId) {
        handleUserOnboard(userId, orgId, positionId, jobTitleId);
    }

    /**
     * 用户调动（Controller别名）
     */
    public void transferUser(Long userId, Long newOrgId, Long newPositionId) {
        handleUserTransfer(userId, newOrgId, newPositionId);
    }

    /**
     * 用户离职（Controller别名）
     */
    public void resignUser(Long userId) {
        handleUserResign(userId);
    }

    /**
     * 用户升降级（Controller别名）
     */
    public void promoteUser(Long userId, Long newJobTitleId) {
        handleUserPromote(userId, newJobTitleId);
    }

    // ========== 企业行业分类 CRUD ==========

    @Transactional(readOnly = true)
    public PageResult<EnterpriseType> listEnterpriseTypes(int page, int size, String keyword, String category) {
        Page<EnterpriseType> pageData = enterpriseTypeRepository.search(keyword, category, PageRequest.of(page, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page + 1, size);
    }

    public EnterpriseType createEnterpriseType(EnterpriseType request) {
        if (enterpriseTypeRepository.existsByTypeCode(request.getTypeCode())) {
            throw new BusinessException("分类编码已存在: " + request.getTypeCode());
        }
        return enterpriseTypeRepository.save(request);
    }

    public EnterpriseType updateEnterpriseType(Long id, EnterpriseType request) {
        EnterpriseType existing = enterpriseTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("行业分类不存在"));
        if (!existing.getTypeCode().equals(request.getTypeCode()) &&
                enterpriseTypeRepository.existsByTypeCode(request.getTypeCode())) {
            throw new BusinessException("分类编码已存在: " + request.getTypeCode());
        }
        existing.setTypeCode(request.getTypeCode());
        existing.setTypeName(request.getTypeName());
        existing.setCategory(request.getCategory());
        existing.setRequiredLicense(request.getRequiredLicense());
        existing.setDescription(request.getDescription());
        existing.setStatus(request.getStatus());
        existing.setSortOrder(request.getSortOrder());
        return enterpriseTypeRepository.save(existing);
    }

    public void deleteEnterpriseType(Long id) {
        if (!enterpriseTypeRepository.existsById(id)) {
            throw new BusinessException("行业分类不存在");
        }
        enterpriseTypeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EnterpriseType> getActiveEnterpriseTypes() {
        return enterpriseTypeRepository.findByStatusOrderBySortOrder(1);
    }

    // ========== 角色权限详情管理 ==========

    @Transactional(readOnly = true)
    public List<RolePermissionDetail> getRolePermissions(Long roleId) {
        return rolePermissionDetailRepository.findByRoleIdAndStatus(roleId, 1);
    }

    @Transactional
    public void updateRolePermissions(Long roleId, List<String> permissionCodes) {
        // Delete existing
        rolePermissionDetailRepository.deleteByRoleId(roleId);
        // Insert new
        for (String code : permissionCodes) {
            String[] parts = code.split(":");
            String module = parts.length > 0 ? parts[0] : "unknown";
            RolePermissionDetail rpd = RolePermissionDetail.builder()
                    .roleId(roleId)
                    .permissionCode(code)
                    .permissionName(code)
                    .module(module)
                    .status(1)
                    .build();
            rolePermissionDetailRepository.save(rpd);
        }
    }

    // ========== 用户权限分配 ==========

    /**
     * Assign role to user
     */
    @Transactional
    public SysUser assignUserRole(Long userId, Long roleId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        SysRole role = sysRoleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        user.setRoleId(roleId);
        return sysUserRepository.save(user);
    }

    /**
     * Assign data scope to user
     */
    @Transactional
    public void assignUserDataScope(Long userId, List<Map<String, Object>> scopes) {
        userDataScopeRepository.deleteByUserId(userId);
        for (Map<String, Object> scope : scopes) {
            String orgType = (String) scope.get("orgType");
            Long orgId = ((Number) scope.get("orgId")).longValue();
            String dataScope = (String) scope.getOrDefault("dataScope", "VIEW");

            UserDataScope uds = UserDataScope.builder()
                    .userId(userId)
                    .orgId(orgId)
                    .orgType(orgType)
                    .dataScope(dataScope)
                    .build();
            userDataScopeRepository.save(uds);
        }
    }

    /**
     * Get user's data scope list
     */
    @Transactional(readOnly = true)
    public List<UserDataScope> getUserDataScopes(Long userId) {
        return userDataScopeRepository.findByUserId(userId);
    }

    /**
     * Check if user has specific permission
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permissionCode) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // Admin has all permissions
        if ("admin".equals(user.getUserType()) || (user.getRoleId() != null && user.getRoleId() == 1L)) {
            return true;
        }

        if (user.getRoleId() == null) return false;

        return rolePermissionDetailRepository.existsByRoleIdAndPermissionCode(user.getRoleId(), permissionCode);
    }

    /**
     * Get user's permission list
     */
    @Transactional(readOnly = true)
    public List<String> getUserPermissions(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // Admin has all permissions
        if ("admin".equals(user.getUserType()) || (user.getRoleId() != null && user.getRoleId() == 1L)) {
            return List.of("enterprise:create", "enterprise:update", "enterprise:delete", "enterprise:view",
                    "inspection:create", "inspection:update", "inspection:delete", "inspection:view",
                    "rectification:create", "rectification:update", "rectification:delete", "rectification:view",
                    "report:create", "report:update", "report:delete", "report:view",
                    "user:create", "user:update", "user:delete", "user:view",
                    "system:config", "appeal:create", "appeal:view");
        }

        if (user.getRoleId() == null) return List.of();

        return rolePermissionDetailRepository.findByRoleIdAndStatus(user.getRoleId(), 1)
                .stream()
                .map(RolePermissionDetail::getPermissionCode)
                .toList();
    }

    /**
     * Get user's data permission context
     */
    @Transactional(readOnly = true)
    public DataPermissionContext getUserDataPermission(Long userId) {
        return dataPermissionService.getDataPermission(userId);
    }

    /**
     * Get all roles with data permission info
     */
    @Transactional(readOnly = true)
    public List<SysRole> listAllRolesWithDataPermission() {
        return sysRoleRepository.findAll();
    }
}
