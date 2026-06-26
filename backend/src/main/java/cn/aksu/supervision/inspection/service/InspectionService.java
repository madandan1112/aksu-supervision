package cn.aksu.supervision.inspection.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.inspection.dto.InspectionSubmitRequest;
import cn.aksu.supervision.inspection.entity.InspectionRecord;
import cn.aksu.supervision.inspection.repository.InspectionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InspectionService {

    private final InspectionRecordRepository inspectionRecordRepository;

    public InspectionRecord submitRecord(Long inspectorId, InspectionSubmitRequest request) {
        InspectionRecord record = InspectionRecord.builder()
                .taskId(request.getTaskId())
                .enterpriseId(request.getEnterpriseId())
                .inspectorId(inspectorId)
                .status("SUBMITTED")
                .formData(request.getFormData())
                .issues(request.getIssues())
                .evidenceImages(request.getEvidenceImages())
                .evidenceVideos(request.getEvidenceVideos())
                .summary(request.getSummary())
                .inspectionTime(LocalDateTime.now())
                .build();

        return inspectionRecordRepository.save(record);
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

    public InspectionRecord generateNotice(Long id) {
        InspectionRecord record = inspectionRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("检查记录不存在"));

        record.setStatus("NOTICE_GENERATED");
        return inspectionRecordRepository.save(record);
    }
}
