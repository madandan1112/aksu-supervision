package cn.aksu.supervision.alert.scheduler;

import cn.aksu.supervision.alert.constants.AlertConstants;
import cn.aksu.supervision.alert.entity.Alert;
import cn.aksu.supervision.alert.repository.AlertRepository;
import cn.aksu.supervision.system.entity.OrgStructure;
import cn.aksu.supervision.system.entity.OrgUserPosition;
import cn.aksu.supervision.system.entity.SysUser;
import cn.aksu.supervision.system.repository.OrgStructureRepository;
import cn.aksu.supervision.system.repository.OrgUserPositionRepository;
import cn.aksu.supervision.system.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 预警督办升级服务
 * 
 * 升级路径：科室执法人员 → 科室科长 → 县/市局领导 → 地区局领导
 * 每次升级延长24h督办时限
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEscalationService {

    /** 最大升级层级：科室执法人员 → 科长 → 局领导 → 地区局领导 */
    private static final int MAX_ESCALATIONS = 3;

    private final AlertRepository alertRepository;
    private final SysUserRepository sysUserRepository;
    private final OrgUserPositionRepository orgUserPositionRepository;
    private final OrgStructureRepository orgStructureRepository;

    /**
     * 检查并升级超时未处理的预警
     * @return 升级的预警数量
     */
    @Transactional
    public int checkAndEscalate() {
        List<Alert> overdue = alertRepository.findOverdueForEscalation(LocalDateTime.now());
        int count = 0;

        for (Alert alert : overdue) {
            try {
                escalateAlert(alert);
                count++;
            } catch (Exception e) {
                log.error("预警升级失败: alertId={}", alert.getId(), e);
            }
        }
        return count;
    }

    /**
     * 升级单条预警：状态置为ESCALATED、升级次数+1、时限再延24h、沿组织链上浮改派、等级升级
     */
    private void escalateAlert(Alert alert) {
        int currentLevel = alert.getEscalateCount() != null ? alert.getEscalateCount() : 0;
        if (currentLevel >= MAX_ESCALATIONS) {
            // 已升到顶层（地区局领导）仍无人处理：保留ESCALATED状态，只顺延时限避免每小时重复升级
            alert.setStatus(AlertConstants.STATUS_ESCALATED);
            alert.setEscalateAt(LocalDateTime.now().plusHours(AlertConstants.ESCALATION_HOURS));
            alertRepository.save(alert);
            return;
        }

        Long newAssignee = findNextLevelAssignee(alert.getAssignedUserId(), currentLevel);

        alert.setEscalateCount(currentLevel + 1);
        alert.setEscalateAt(LocalDateTime.now().plusHours(AlertConstants.ESCALATION_HOURS));
        alert.setStatus(AlertConstants.STATUS_ESCALATED);

        if (newAssignee != null) {
            alert.setAssignedUserId(newAssignee);
            sysUserRepository.findById(newAssignee).ifPresent(u -> alert.setAssignedUserName(u.getRealName()));
        }

        // 高等级升级
        if (AlertConstants.LEVEL_LOW.equals(alert.getLevel())) {
            alert.setLevel(AlertConstants.LEVEL_MEDIUM);
        } else if (AlertConstants.LEVEL_MEDIUM.equals(alert.getLevel())) {
            alert.setLevel(AlertConstants.LEVEL_HIGH);
        }

        alertRepository.save(alert);
        log.info("预警升级: id={}, escalateCount={}, newAssignee={}, newLevel={}",
                alert.getId(), alert.getEscalateCount(), alert.getAssignedUserName(), alert.getLevel());
    }

    /**
     * 查找下一级督办人员
     * 升级路径：执法人员 → 科长 → 局领导 → 地区局领导
     */
    private Long findNextLevelAssignee(Long currentAssignee, int escalateCount) {
        if (currentAssignee == null) return null;

        Optional<SysUser> current = sysUserRepository.findById(currentAssignee);
        if (current.isEmpty()) return null;

        // 查找当前处理人的组织归属
        List<OrgUserPosition> positions = orgUserPositionRepository.findByUserId(currentAssignee);
        if (positions.isEmpty()) return null;

        Long orgId = positions.get(0).getOrgId();
        OrgStructure org = orgStructureRepository.findById(orgId).orElse(null);
        if (org == null) return null;

        // 根据升级次数向上查找
        Long parentOrgId = org.getParentId();
        for (int i = 0; i < escalateCount && parentOrgId != null; i++) {
            OrgStructure parent = orgStructureRepository.findById(parentOrgId).orElse(null);
            if (parent != null) {
                parentOrgId = parent.getParentId();
            } else {
                break;
            }
        }

        // 在上级组织中找负责人
        if (parentOrgId != null) {
            List<OrgUserPosition> parentPositions = orgUserPositionRepository.findByOrgIdAndStatus(parentOrgId, 1);
            for (OrgUserPosition pos : parentPositions) {
                if (pos.getUserId() != null) {
                    return pos.getUserId();
                }
            }
        }

        return null;
    }
}
