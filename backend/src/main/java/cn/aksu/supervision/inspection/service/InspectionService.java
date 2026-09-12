package cn.aksu.supervision.inspection.service;

import cn.aksu.supervision.alert.listener.InspectionFailedEvent;
import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.inspection.dto.InspectionSubmitRequest;
import cn.aksu.supervision.inspection.entity.InspectionRecord;
import cn.aksu.supervision.inspection.repository.InspectionRecordRepository;
import cn.aksu.supervision.system.entity.SysUser;
import cn.aksu.supervision.system.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRecordRepository inspectionRecordRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final SysUserRepository sysUserRepository;
    private final RectificationNoticeGenerator noticeGenerator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public InspectionRecord submitRecord(Long inspectorId, InspectionSubmitRequest request) {
        // 保存检查记录
        InspectionRecord record = InspectionRecord.builder()
                .taskId(request.getTaskId() != null ? request.getTaskId() : 0L)
                .enterpriseId(request.getEnterpriseId())
                .inspectorId(inspectorId)
                .status("SUBMITTED")
                .formData(request.getFormData())
                .issues(request.getIssues())
                .checkType(request.getCheckTypes())
                .evidenceImages(request.getEvidenceImages())
                .evidenceVideos(request.getEvidenceVideos())
                .attachmentUrls(request.getAttachmentUrls())
                .summary(request.getSummary())
                .inspectionTime(LocalDateTime.now())
                .build();

        // 获取企业名称和执法人员姓名
        Enterprise enterprise = enterpriseRepository.findById(request.getEnterpriseId()).orElse(null);
        if (enterprise != null) {
            record.setEnterpriseName(enterprise.getName());
        }
        SysUser inspector = sysUserRepository.findById(inspectorId).orElse(null);
        if (inspector != null) {
            record.setInspectorName(inspector.getRealName() != null ? inspector.getRealName() : inspector.getUsername());
        }

        inspectionRecordRepository.save(record);

        // 生成整改通知书（独立事务，失败不影响检查记录保存）
        try {
            noticeGenerator.generate(record, request, inspectorId);
        } catch (Exception e) {
            log.error("生成整改通知书失败(不影响检查记录保存): {}", e.getMessage());
        }

        // 检查发现问题（有问题描述）时发布 InspectionFailedEvent，触发实时预警（异步，失败不影响主流程）
        if (request.getIssues() != null && !request.getIssues().isBlank()) {
            try {
                eventPublisher.publishEvent(new InspectionFailedEvent(this, record.getEnterpriseId(),
                        Map.of(
                                "issues", request.getIssues(),
                                "checkType", request.getCheckTypes() != null ? request.getCheckTypes() : "on-site",
                                "inspector", record.getInspectorName() != null ? record.getInspectorName() : "",
                                "recordId", record.getId()
                        )));
            } catch (Exception e) {
                log.error("发布检查不合格事件失败: {}", e.getMessage());
            }
        }

        return record;
    }

    @Transactional(readOnly = true)
    public PageResult<InspectionRecord> listByInspector(Long inspectorId, int page, int size) {
        Page<InspectionRecord> pageData = inspectionRecordRepository.findByInspectorIdOrderByCreateTimeDesc(
                inspectorId, PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public InspectionRecord getRecordDetail(Long id) {
        return inspectionRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("检查记录不存在"));
    }

    @Transactional
    public InspectionRecord generateNotice(Long id) {
        InspectionRecord record = inspectionRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("检查记录不存在"));
        record.setStatus("NOTICE_GENERATED");
        return inspectionRecordRepository.save(record);
    }
}
