package cn.aksu.supervision.alert.engine;

import cn.aksu.supervision.alert.constants.AlertConstants;
import cn.aksu.supervision.alert.entity.Alert;
import cn.aksu.supervision.alert.repository.AlertRepository;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.entity.EnterpriseRegistration;
import cn.aksu.supervision.enterprise.repository.EnterpriseRegistrationRepository;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.inspection.entity.InspectionRecord;
import cn.aksu.supervision.inspection.repository.InspectionRecordRepository;
import cn.aksu.supervision.rectification.entity.RectificationNotice;
import cn.aksu.supervision.rectification.repository.RectificationNoticeRepository;
import cn.aksu.supervision.report.entity.ComplianceReport;
import cn.aksu.supervision.report.repository.ComplianceReportRepository;
import cn.aksu.supervision.system.entity.OrgStructure;
import cn.aksu.supervision.system.entity.OrgUserPosition;
import cn.aksu.supervision.system.entity.SysUser;
import cn.aksu.supervision.system.repository.OrgStructureRepository;
import cn.aksu.supervision.system.repository.OrgUserPositionRepository;
import cn.aksu.supervision.system.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预警规则引擎
 * 核心职责：
 * 1. 执行12条预警规则判定
 * 2. 去重（同企业同类型24h内不重复）
 * 3. 自动派单（关联属地监管人员）
 * 4. 生成Alert记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertRuleEngine {

    /** 整改单关闭态：验收通过(ACCEPTED)与历史COMPLETED均视为已关闭 */
    private static final List<String> RECT_CLOSED_STATUSES = List.of("ACCEPTED", "COMPLETED");

    private final AlertRepository alertRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ComplianceReportRepository complianceReportRepository;
    private final RectificationNoticeRepository rectificationNoticeRepository;
    private final InspectionRecordRepository inspectionRecordRepository;
    private final EnterpriseRegistrationRepository registrationRepository;
    private final OrgStructureRepository orgStructureRepository;
    private final OrgUserPositionRepository orgUserPositionRepository;
    private final SysUserRepository sysUserRepository;

    /**
     * 执行全部定时扫描规则，返回新生成的预警数量
     */
    public int executeScheduledRules() {
        int count = 0;
        count += scanLicenseExpiring();
        count += scanLicenseExpired();
        count += scanRectificationOverdue();
        count += scanInspectionOverdue();
        count += scanReportMissing();
        count += scanRegistrationAnomaly();
        count += scanCreditAnomaly();
        count += scanRepeatViolation();
        log.info("定时扫描完成，新增预警 {} 条", count);
        return count;
    }

    /**
     * 执行事件驱动规则（检查不合格、审批退回等）
     */
    public Alert fireEventRule(String ruleType, Long enterpriseId, Map<String, Object> context) {
        return switch (ruleType) {
            case "INSPECTION_FAILED" -> generateInspectionFailedAlert(enterpriseId, context);
            case "REGISTRATION_REJECTED" -> generateRegistrationRejectedAlert(enterpriseId, context);
            case "RECTIFICATION_SUBMITTED" -> generateRectificationSubmittedAlert(enterpriseId, context);
            default -> null;
        };
    }

    // ==================== 资质到期类规则（4条） ====================

    /** 规则1: 许可证到期前60天 → LOW */
    private int scanLicenseExpiring() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        // 60天预警
        List<ComplianceReport> reports60 = complianceReportRepository.findExpiringReports(now, now.plusDays(60));
        for (ComplianceReport r : reports60) {
            if (r.getExpireDate().isAfter(now.plusDays(30))) {
                count += createAlertIfNotDuplicate(r.getEnterpriseId(), AlertConstants.TYPE_LICENSE_EXPIRING,
                        AlertConstants.LEVEL_LOW, buildLicenseTitle(r, 60), buildLicenseContent(r, 60));
            }
        }

        // 30天预警
        List<ComplianceReport> reports30 = complianceReportRepository.findExpiringReports(now, now.plusDays(30));
        for (ComplianceReport r : reports30) {
            if (r.getExpireDate().isAfter(now.plusDays(7))) {
                count += createAlertIfNotDuplicate(r.getEnterpriseId(), AlertConstants.TYPE_LICENSE_EXPIRING,
                        AlertConstants.LEVEL_MEDIUM, buildLicenseTitle(r, 30), buildLicenseContent(r, 30));
            }
        }

        // 7天预警
        List<ComplianceReport> reports7 = complianceReportRepository.findExpiringReports(now, now.plusDays(7));
        for (ComplianceReport r : reports7) {
            count += createAlertIfNotDuplicate(r.getEnterpriseId(), AlertConstants.TYPE_LICENSE_EXPIRING,
                    AlertConstants.LEVEL_HIGH, buildLicenseTitle(r, 7), buildLicenseContent(r, 7));
        }

        return count;
    }

    /** 规则2: 许可证已过期 → HIGH */
    private int scanLicenseExpired() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (ComplianceReport r : complianceReportRepository.findExpiredReports(now)) {
            String entName = getEnterpriseName(r.getEnterpriseId());
            count += createAlertIfNotDuplicate(r.getEnterpriseId(), AlertConstants.TYPE_LICENSE_EXPIRED,
                    AlertConstants.LEVEL_HIGH,
                    "【已过期】" + r.getTitle() + " - " + entName,
                    "企业" + entName + "的" + r.getTitle() + "已于" + r.getExpireDate().toLocalDate() + "过期，请立即督促企业办理续期或依法处置。");
        }
        return count;
    }

    // ==================== 监管流程类规则（4条） ====================

    /** 规则3: 整改超期未反馈 → HIGH */
    private int scanRectificationOverdue() {
        LocalDateTime now = LocalDateTime.now();
        List<RectificationNotice> allNotices = rectificationNoticeRepository.findByStatusNotIn(RECT_CLOSED_STATUSES);
        int count = 0;

        for (RectificationNotice n : allNotices) {
            if (!RECT_CLOSED_STATUSES.contains(n.getStatus()) && n.getDeadline() != null && n.getDeadline().plusDays(3).isBefore(now)) {
                String entName = getEnterpriseName(n.getEnterpriseId());
                long overdueDays = java.time.Duration.between(n.getDeadline(), now).toDays();
                count += createAlertIfNotDuplicate(n.getEnterpriseId(), AlertConstants.TYPE_RECTIFICATION_OVERDUE,
                        AlertConstants.LEVEL_HIGH,
                        "整改超期未反馈 - " + entName,
                        "企业" + entName + "的整改通知（编号" + n.getNoticeNo() + "）已超期" + overdueDays + "天未反馈，要求限期" + n.getDeadline().toLocalDate() + "，请立即督办。");
            }
        }
        return count;
    }

    /** 规则4: 检查任务超期 → MEDIUM */
    private int scanInspectionOverdue() {
        LocalDateTime now = LocalDateTime.now();
        List<InspectionRecord> drafts = inspectionRecordRepository.findByStatusOrderByCreateTimeDesc("DRAFT",
                org.springframework.data.domain.PageRequest.of(0, 1000)).getContent();
        int count = 0;

        for (InspectionRecord r : drafts) {
            // 草稿状态超过7天未提交视为超期
            if (r.getCreateTime() != null && r.getCreateTime().plusDays(7).isBefore(now)) {
                String entName = r.getEnterpriseName() != null ? r.getEnterpriseName() : getEnterpriseName(r.getEnterpriseId());
                count += createAlertIfNotDuplicate(r.getEnterpriseId(), AlertConstants.TYPE_INSPECTION_OVERDUE,
                        AlertConstants.LEVEL_MEDIUM,
                        "检查任务超期 - " + entName,
                        "企业" + entName + "的现场检查任务已创建" + java.time.Duration.between(r.getCreateTime(), now).toDays() + "天仍未完成，请督促执法人员尽快执行。");
            }
        }
        return count;
    }

    /** 规则5: 合规报告未提交 → MEDIUM */
    private int scanReportMissing() {
        LocalDateTime now = LocalDateTime.now();
        List<Enterprise> allEnterprises = enterpriseRepository.findAll();
        int count = 0;

        for (Enterprise e : allEnterprises) {
            if (e.getStatus() != 1) continue; // 已注销的不检查
            // 查该企业最近是否有APPROVED状态的报告
            List<ComplianceReport> reports = complianceReportRepository.findByEnterpriseIdOrderByCreateTimeDesc(e.getId(),
                    org.springframework.data.domain.PageRequest.of(0, 1)).getContent();
            boolean hasRecentReport = false;
            if (!reports.isEmpty()) {
                ComplianceReport latest = reports.get(0);
                // 有报告且在最近45天内提交的视为正常
                if (latest.getCreateTime() != null && latest.getCreateTime().isAfter(now.minusDays(45))) {
                    hasRecentReport = true;
                }
            }
            if (!hasRecentReport && e.getCreateTime() != null && e.getCreateTime().plusDays(30).isBefore(now)) {
                count += createAlertIfNotDuplicate(e.getId(), AlertConstants.TYPE_REPORT_MISSING,
                        AlertConstants.LEVEL_MEDIUM,
                        "合规报告未提交 - " + e.getName(),
                        "企业" + e.getName() + "超过45天未提交合规报告，请督促企业按期上报。");
            }
        }
        return count;
    }

    /** 规则6: 注册备案异常 → LOW */
    private int scanRegistrationAnomaly() {
        LocalDateTime now = LocalDateTime.now();
        List<EnterpriseRegistration> pendingRegs = registrationRepository.findByStatusOrderByCreateTimeDesc("REJECTED");
        int count = 0;

        for (EnterpriseRegistration reg : pendingRegs) {
            if (reg.getUpdateTime() != null && reg.getUpdateTime().plusDays(30).isBefore(now)) {
                String entName = reg.getEnterpriseName() != null ? reg.getEnterpriseName() : "未知企业";
                count += createAlertIfNotDuplicate(null, AlertConstants.TYPE_REGISTRATION_ANOMALY,
                        AlertConstants.LEVEL_LOW,
                        "注册备案异常 - " + entName,
                        "企业" + entName + "的注册申请被退回已超过30天未重新提交，可能存在经营异常。");
            }
        }

        // 注册后30天仍SUBMITTED未审核（注册状态机为DRAFT/SUBMITTED/APPROVED/REJECTED）
        List<EnterpriseRegistration> pendings = registrationRepository.findByStatusOrderByCreateTimeDesc("SUBMITTED");
        for (EnterpriseRegistration reg : pendings) {
            if (reg.getCreateTime() != null && reg.getCreateTime().plusDays(30).isBefore(now)) {
                String entName = reg.getEnterpriseName() != null ? reg.getEnterpriseName() : "未知企业";
                count += createAlertIfNotDuplicate(null, AlertConstants.TYPE_REGISTRATION_ANOMALY,
                        AlertConstants.LEVEL_LOW,
                        "注册审核超时 - " + entName,
                        "企业" + entName + "的注册申请已提交30天仍未审核，请尽快处理。");
            }
        }
        return count;
    }

    // ==================== 信用风险类规则（4条） ====================

    /** 规则7: 企业被列入异常 → HIGH */
    private int scanCreditAnomaly() {
        int count = 0;

        Set<Long> anomalyIds = new HashSet<>();
        for (Enterprise e : enterpriseRepository.findByStatus(0)) {
            anomalyIds.add(e.getId());
            count += createAlertIfNotDuplicate(e.getId(), AlertConstants.TYPE_CREDIT_ANOMALY,
                    AlertConstants.LEVEL_HIGH,
                    "企业状态异常 - " + e.getName(),
                    "企业" + e.getName() + "当前状态为已注销，请关注是否存在异常经营行为。");
        }
        for (Enterprise e : enterpriseRepository.findByRegistrationStatus("CLOSED")) {
            if (anomalyIds.add(e.getId())) {
                count += createAlertIfNotDuplicate(e.getId(), AlertConstants.TYPE_CREDIT_ANOMALY,
                        AlertConstants.LEVEL_HIGH,
                        "企业状态异常 - " + e.getName(),
                        "企业" + e.getName() + "当前状态为已关闭，请关注是否存在异常经营行为。");
            }
        }
        return count;
    }

    /** 规则8: 屡次违规 → HIGH */
    private int scanRepeatViolation() {
        LocalDateTime since = LocalDateTime.now().minusMonths(12);
        List<Enterprise> allEnterprises = enterpriseRepository.findAll();
        int count = 0;

        for (Enterprise e : allEnterprises) {
            // 同一企业12个月内3次以上整改通知
            long rectCount = rectificationNoticeRepository.countByEnterpriseIdAndStatusNotIn(e.getId(), RECT_CLOSED_STATUSES);
            long alertCount = alertRepository.countByEnterpriseAndTypesSince(e.getId(),
                    List.of(AlertConstants.TYPE_RECTIFICATION_OVERDUE, AlertConstants.TYPE_INSPECTION_OVERDUE, AlertConstants.TYPE_REPEAT_VIOLATION),
                    since);

            if (rectCount >= 3 || alertCount >= 3) {
                count += createAlertIfNotDuplicate(e.getId(), AlertConstants.TYPE_REPEAT_VIOLATION,
                        AlertConstants.LEVEL_HIGH,
                        "屡次违规预警 - " + e.getName(),
                        "企业" + e.getName() + "在12个月内累计" + Math.max(rectCount, alertCount) + "次违规/整改记录，属于高风险企业，建议加大监管力度。");
            }
        }
        return count;
    }

    // ==================== 事件驱动规则 ====================

    /** 检查不合格触发 */
    private Alert generateInspectionFailedAlert(Long enterpriseId, Map<String, Object> context) {
        String entName = getEnterpriseName(enterpriseId);
        String issues = context.getOrDefault("issues", "未提供具体问题").toString();
        return createAlert(enterpriseId, AlertConstants.TYPE_CREDIT_ANOMALY,
                AlertConstants.LEVEL_HIGH,
                "现场检查不合格 - " + entName,
                "企业" + entName + "现场检查发现不合格项：" + issues + "，请及时安排整改。",
                AlertConstants.SOURCE_EVENT);
    }

    /** 注册审批退回触发 */
    private Alert generateRegistrationRejectedAlert(Long enterpriseId, Map<String, Object> context) {
        // 被退回的企业通常尚未建档（enterpriseId为null），优先取事件里携带的企业名
        String entName = "未知企业";
        if (enterpriseId != null) {
            entName = getEnterpriseName(enterpriseId);
        } else if (context.get("enterpriseName") != null) {
            entName = context.get("enterpriseName").toString();
        }
        String reason = context.getOrDefault("reason", "未提供退回原因").toString();
        return createAlert(enterpriseId, AlertConstants.TYPE_REGISTRATION_ANOMALY,
                AlertConstants.LEVEL_LOW,
                "注册审批被退回 - " + entName,
                "企业" + entName + "的注册申请被退回，原因：" + reason + "，请关注是否需要重新提交。",
                AlertConstants.SOURCE_EVENT);
    }

    /** 整改反馈提交触发（检查是否仍有未完成整改） */
    private Alert generateRectificationSubmittedAlert(Long enterpriseId, Map<String, Object> context) {
        List<RectificationNotice> remaining = rectificationNoticeRepository.findByEnterpriseIdAndStatusNotIn(enterpriseId, RECT_CLOSED_STATUSES);
        if (remaining.size() > 1) {
            String entName = getEnterpriseName(enterpriseId);
            return createAlert(enterpriseId, AlertConstants.TYPE_RECTIFICATION_OVERDUE,
                    AlertConstants.LEVEL_MEDIUM,
                    "整改仍有未完成项 - " + entName,
                    "企业" + entName + "已反馈部分整改，但仍有" + (remaining.size() - 1) + "项整改未完成，请持续跟进。",
                    AlertConstants.SOURCE_EVENT);
        }
        return null;
    }

    // ==================== 核心生成逻辑 ====================

    /**
     * 去重+生成预警
     * @return 1=新生成, 0=已存在被跳过
     */
    private int createAlertIfNotDuplicate(Long enterpriseId, String alertType, String level, String title, String content) {
        LocalDateTime since = LocalDateTime.now().minusHours(AlertConstants.DEDUP_HOURS);

        if (enterpriseId != null) {
            boolean exists = alertRepository.existsDuplicate(enterpriseId, alertType, since);
            if (exists) {
                log.debug("预警去重跳过: enterpriseId={}, type={}", enterpriseId, alertType);
                return 0;
            }
        } else {
            // enterpriseId为null时（注册备案异常等），按标题+类型去重，避免事件重复触发刷屏
            boolean exists = alertRepository.existsDuplicateByTitle(alertType, title, since);
            if (exists) {
                log.debug("预警去重跳过(按标题): type={}, title={}", alertType, title);
                return 0;
            }
        }

        createAlert(enterpriseId, alertType, level, title, content, AlertConstants.SOURCE_SCHEDULED);
        return 1;
    }

    /**
     * 生成Alert记录 + 自动派单
     */
    private Alert createAlert(Long enterpriseId, String alertType, String level, String title, String content, String source) {
        // 自动派单：查找属地监管人员
        Long assignedUserId = null;

        String[] assignedUserNameHolder = new String[1];
        if (enterpriseId != null) {
            Optional<Enterprise> entOpt = enterpriseRepository.findById(enterpriseId);
            if (entOpt.isPresent()) {
                Enterprise ent = entOpt.get();
                long[] assigned = findAssignedOfficer(ent.getArea());
                assignedUserId = assigned[0] > 0 ? assigned[0] : null;
                if (assignedUserId != null) {
                    sysUserRepository.findById(assignedUserId).ifPresent(u -> assignedUserNameHolder[0] = u.getRealName());
                }
            }
        }
        String assignedUserName = assignedUserNameHolder[0];

        Alert alert = Alert.builder()
                .alertType(alertType)
                .level(level)
                .enterpriseId(enterpriseId)
                .enterpriseName(enterpriseId != null ? getEnterpriseName(enterpriseId) : null)
                .title(title)
                .content(content)
                .status(AlertConstants.STATUS_PENDING)
                .source(source)
                .assignedUserId(assignedUserId)
                .assignedUserName(assignedUserName)
                .escalateAt(LocalDateTime.now().plusHours(AlertConstants.ESCALATION_HOURS))
                .escalateCount(0)
                .build();

        Alert saved = alertRepository.save(alert);
        log.info("生成预警: id={}, type={}, level={}, enterpriseId={}, assignedTo={}",
                saved.getId(), alertType, level, enterpriseId, assignedUserName);
        return saved;
    }

    /**
     * 根据企业所在区域查找属地执法人员
     * 优先找该区域的执法人员，其次找该区域科室科长
     */
    private long[] findAssignedOfficer(String area) {
        if (area == null || area.isEmpty()) return new long[]{0};

        // 查找区域对应的组织
        List<OrgStructure> orgs = orgStructureRepository.findAll();
        OrgStructure targetOrg = null;
        for (OrgStructure org : orgs) {
            if (org.getName() != null && org.getName().contains(area.replace("市", "").replace("县", ""))) {
                if (org.getLevel() != null && (org.getLevel() == 3 || org.getLevel() == 4)) {
                    targetOrg = org;
                    break;
                }
            }
        }

        if (targetOrg == null) return new long[]{0};

        // 找该组织下的执法人员
        List<OrgUserPosition> positions = orgUserPositionRepository.findByOrgIdAndStatus(targetOrg.getId(), 1);
        for (OrgUserPosition pos : positions) {
            if (pos.getUserId() != null) {
                Optional<SysUser> userOpt = sysUserRepository.findById(pos.getUserId());
                if (userOpt.isPresent() && "inspector".equals(userOpt.get().getUserType())) {
                    return new long[]{pos.getUserId()};
                }
            }
        }

        // 没有执法人员则找科长
        for (OrgUserPosition pos : positions) {
            if (pos.getUserId() != null) {
                return new long[]{pos.getUserId()};
            }
        }

        return new long[]{0};
    }

    // ==================== 辅助方法 ====================

    private String getEnterpriseName(Long enterpriseId) {
        if (enterpriseId == null) return "未知企业";
        return enterpriseRepository.findById(enterpriseId).map(Enterprise::getName).orElse("未知企业");
    }

    private String buildLicenseTitle(ComplianceReport r, int days) {
        String entName = getEnterpriseName(r.getEnterpriseId());
        return "【" + days + "天到期】" + r.getTitle() + " - " + entName;
    }

    private String buildLicenseContent(ComplianceReport r, int days) {
        String entName = getEnterpriseName(r.getEnterpriseId());
        return "企业" + entName + "的" + r.getTitle() + "将于" + r.getExpireDate().toLocalDate() +
                "到期（剩余" + days + "天），请督促企业及时办理续期手续。";
    }
}
