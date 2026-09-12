package cn.aksu.supervision.rectification.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.message.service.MessageService;
import cn.aksu.supervision.ocr.service.OcrService;
import cn.aksu.supervision.rectification.dto.AcceptanceRequest;
import cn.aksu.supervision.rectification.dto.FeedbackSubmitRequest;
import cn.aksu.supervision.rectification.entity.AcceptanceRecord;
import cn.aksu.supervision.rectification.entity.RectificationFeedback;
import cn.aksu.supervision.rectification.entity.RectificationNotice;
import cn.aksu.supervision.rectification.repository.AcceptanceRecordRepository;
import cn.aksu.supervision.rectification.repository.RectificationFeedbackRepository;
import cn.aksu.supervision.rectification.repository.RectificationNoticeRepository;
import cn.aksu.supervision.alert.listener.RectificationSubmittedEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RectificationService {

    private final RectificationNoticeRepository noticeRepository;
    private final RectificationFeedbackRepository feedbackRepository;
    private final AcceptanceRecordRepository acceptanceRecordRepository;
    private final OcrService ocrService;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public PageResult<RectificationNotice> listByEnterprise(Long enterpriseId, int page, int size) {
        Page<RectificationNotice> pageData = noticeRepository.findByEnterpriseIdOrderByCreateTimeDesc(
                enterpriseId, PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getNoticeDetail(Long id) {
        RectificationNotice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("整改通知书不存在"));

        List<RectificationFeedback> feedbacks = feedbackRepository.findByNoticeIdOrderByCreateTimeDesc(id);
        List<AcceptanceRecord> acceptances = acceptanceRecordRepository.findByNoticeIdOrderByCreateTimeDesc(id);

        Map<String, Object> detail = new HashMap<>();
        detail.put("notice", notice);
        detail.put("feedbacks", feedbacks);
        detail.put("acceptances", acceptances);
        return detail;
    }

    public RectificationFeedback submitFeedback(Long noticeId, Long enterpriseId, FeedbackSubmitRequest request) {
        RectificationNotice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException("整改通知书不存在"));

        if (!notice.getEnterpriseId().equals(enterpriseId)) {
            throw new BusinessException("无权操作此整改通知");
        }

        RectificationFeedback feedback = RectificationFeedback.builder()
                .noticeId(noticeId)
                .enterpriseId(enterpriseId)
                .rectifyMeasures(request.getRectifyMeasures())
                .evidenceImages(request.getEvidenceImages())
                .evidenceVideos(request.getEvidenceVideos())
                .remark(request.getRemark())
                .checkType(request.getCheckType())
                .attachmentUrls(request.getAttachmentUrls())
                .status("SUBMITTED")
                .build();

        feedbackRepository.save(feedback);

        notice.setStatus("FEEDBACK_SUBMITTED");
        noticeRepository.save(notice);

        // 发布整改反馈事件，触发预警（检查是否仍有未完成整改）
        eventPublisher.publishEvent(new RectificationSubmittedEvent(this, enterpriseId,
                Map.of("noticeId", noticeId, "enterpriseId", enterpriseId)));

        // 异步尝试OCR识别附件内容并发送通知给执法人员
        sendOcrNotification(feedback, notice);

        return feedback;
    }

    /**
     * OCR识别附件内容 + 发送点对点通知给当时检查的执法人员
     */
    private void sendOcrNotification(RectificationFeedback feedback, RectificationNotice notice) {
        try {
            StringBuilder ocrSummary = new StringBuilder();

            // 1. OCR识别整改后图片
            if (feedback.getEvidenceImages() != null && !feedback.getEvidenceImages().isEmpty()) {
                try {
                    List<String> images = objectMapper.readValue(feedback.getEvidenceImages(),
                            new TypeReference<List<String>>() {});
                    if (!images.isEmpty()) {
                        ocrSummary.append("📸 整改后图片识别内容:\n");
                        for (int i = 0; i < images.size(); i++) {
                            try {
                                String text = ocrService.recognizeText(images.get(i));
                                if (!text.isEmpty()) {
                                    ocrSummary.append("  图片").append(i + 1).append(": ").append(text, 0, Math.min(text.length(), 200)).append("\n");
                                }
                            } catch (Exception e) {
                                log.warn("OCR识别整改后图片{}失败: {}", i + 1, e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析evidenceImages JSON失败: {}", e.getMessage());
                }
            }

            // 2. OCR识别附件
            if (feedback.getAttachmentUrls() != null && !feedback.getAttachmentUrls().isEmpty()) {
                try {
                    List<String> attachments = objectMapper.readValue(feedback.getAttachmentUrls(),
                            new TypeReference<List<String>>() {});
                    if (!attachments.isEmpty()) {
                        ocrSummary.append("📎 附件识别内容:\n");
                        for (int i = 0; i < attachments.size(); i++) {
                            try {
                                String text = ocrService.recognizeText(attachments.get(i));
                                if (!text.isEmpty()) {
                                    ocrSummary.append("  附件").append(i + 1).append(": ").append(text, 0, Math.min(text.length(), 200)).append("\n");
                                }
                            } catch (Exception e) {
                                log.warn("OCR识别附件{}失败: {}", i + 1, e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析attachmentUrls JSON失败: {}", e.getMessage());
                }
            }

            // 3. 构造通知消息发送给执法人员
            Long inspectorId = notice.getInspectorId();
            if (inspectorId != null) {
                String checkTypeLabel = feedback.getCheckType() != null ? feedback.getCheckType() : "未指定";
                String title = "企业已提交整改反馈";
                String content = "整改通知编号: " + notice.getNoticeNo() +
                        "\n检查类型: " + checkTypeLabel +
                        "\n整改措施: " + (feedback.getRectifyMeasures() != null ?
                        feedback.getRectifyMeasures().substring(0, Math.min(feedback.getRectifyMeasures().length(), 100)) : "无");

                if (ocrSummary.length() > 0) {
                    content += "\n\n--- OCR识别结果 ---\n" + ocrSummary;
                }

                try {
                    messageService.sendMessage(
                            inspectorId,
                            "RECTIFICATION_FEEDBACK",
                            title,
                            content,
                            "/inspector/rectification/" + notice.getId(),
                            notice.getId()
                    );
                    log.info("整改反馈通知已发送给执法人员 inspectorId={}", inspectorId);
                } catch (Exception e) {
                    log.warn("发送整改反馈通知失败: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            // OCR识别失败不影响主流程
            log.error("整改反馈OCR通知流程异常(不影响反馈提交): {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PageResult<RectificationNotice> listPendingAcceptance(Long inspectorId, int page, int size) {
        Page<RectificationNotice> pageData = noticeRepository.findByStatusOrderByCreateTimeDesc(
                "FEEDBACK_SUBMITTED", PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public AcceptanceRecord accept(Long noticeId, Long inspectorId, AcceptanceRequest request) {
        RectificationNotice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException("整改通知书不存在"));

        AcceptanceRecord record = AcceptanceRecord.builder()
                .noticeId(noticeId)
                .inspectorId(inspectorId)
                .conclusion(request.getConclusion())
                .opinion(request.getOpinion())
                .evidenceImages(request.getEvidenceImages())
                .build();

        acceptanceRecordRepository.save(record);

        if ("PASS".equals(request.getConclusion())) {
            notice.setStatus("ACCEPTED");
        } else {
            notice.setStatus("REJECTED");
        }
        noticeRepository.save(notice);

        return record;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> compareForAI(Long id) {
        RectificationNotice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("整改通知书不存在"));

        List<RectificationFeedback> feedbacks = feedbackRepository.findByNoticeIdOrderByCreateTimeDesc(id);

        Map<String, Object> compareData = new HashMap<>();
        compareData.put("notice", notice);
        compareData.put("latestFeedback", feedbacks.isEmpty() ? null : feedbacks.get(0));
        compareData.put("issues", notice.getIssues());
        compareData.put("requirements", notice.getRequirements());
        if (!feedbacks.isEmpty()) {
            compareData.put("rectifyMeasures", feedbacks.get(0).getRectifyMeasures());
            compareData.put("evidenceImages", feedbacks.get(0).getEvidenceImages());
        }
        return compareData;
    }
}
