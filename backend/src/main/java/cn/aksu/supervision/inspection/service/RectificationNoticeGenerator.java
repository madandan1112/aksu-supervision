package cn.aksu.supervision.inspection.service;

import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.inspection.dto.InspectionSubmitRequest;
import cn.aksu.supervision.inspection.entity.InspectionRecord;
import cn.aksu.supervision.message.service.MessageService;
import cn.aksu.supervision.rectification.entity.RectificationNotice;
import cn.aksu.supervision.rectification.repository.RectificationNoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class RectificationNoticeGenerator {

    private final RectificationNoticeRepository noticeRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final MessageService messageService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generate(InspectionRecord record, InspectionSubmitRequest request, Long inspectorId) {
        try {
            String noticeNo = "ZG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + String.format("%04d", (int) (Math.random() * 10000));

            LocalDateTime deadline = LocalDateTime.now().plusDays(15);
            if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
                try {
                    deadline = LocalDateTime.parse(request.getDeadline());
                } catch (Exception e) {
                    log.warn("deadline parse failed, using default 15 days: {}", e.getMessage());
                }
            }

            RectificationNotice notice = RectificationNotice.builder()
                    .inspectionRecordId(record.getId())
                    .enterpriseId(record.getEnterpriseId())
                    .inspectorId(inspectorId)
                    .noticeNo(noticeNo)
                    .issues(request.getIssues() != null ? request.getIssues() : "")
                    .requirements(request.getRequirements() != null ? request.getRequirements() : "")
                    .deadline(deadline)
                    .attachmentUrl(request.getEvidenceImages())
                    .status("ISSUED")
                    .build();

            noticeRepository.save(notice);

            Enterprise enterprise = enterpriseRepository.findById(record.getEnterpriseId()).orElse(null);
            if (enterprise != null && enterprise.getUserId() != null) {
                StringBuilder content = new StringBuilder();
                content.append("checkType: ").append(request.getCheckTypes() != null ? request.getCheckTypes() : "on-site").append("\n");
                content.append("issues: ").append(request.getIssues() != null ? request.getIssues().substring(0, Math.min(request.getIssues().length(), 100)) : "").append("\n");
                content.append("requirements: ").append(request.getRequirements() != null ? request.getRequirements().substring(0, Math.min(request.getRequirements().length(), 100)) : "").append("\n");
                content.append("deadline: ").append(deadline.toLocalDate()).append("\n");
                content.append("inspector: ").append(record.getInspectorName() != null ? record.getInspectorName() : "").append("\n");

                messageService.sendMessage(
                        enterprise.getUserId(),
                        "enterprise",
                        "RECTIFICATION_NOTICE",
                        "Rectification Notice: " + noticeNo,
                        content.toString(),
                        "/rectification/" + notice.getId(),
                        notice.getId()
                );
                log.info("Rectification notice sent to enterprise userId={}", enterprise.getUserId());
            }

            log.info("Rectification notice generated noticeNo={}, recordId={}", noticeNo, record.getId());
        } catch (Exception e) {
            log.error("Failed to generate rectification notice: {}", e.getMessage(), e);
            throw e; // let REQUIRES_NEW transaction roll back independently
        }
    }
}
