package cn.aksu.supervision.rectification.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.rectification.dto.AcceptanceRequest;
import cn.aksu.supervision.rectification.dto.FeedbackSubmitRequest;
import cn.aksu.supervision.rectification.entity.AcceptanceRecord;
import cn.aksu.supervision.rectification.entity.RectificationFeedback;
import cn.aksu.supervision.rectification.entity.RectificationNotice;
import cn.aksu.supervision.rectification.repository.AcceptanceRecordRepository;
import cn.aksu.supervision.rectification.repository.RectificationFeedbackRepository;
import cn.aksu.supervision.rectification.repository.RectificationNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RectificationService {

    private final RectificationNoticeRepository noticeRepository;
    private final RectificationFeedbackRepository feedbackRepository;
    private final AcceptanceRecordRepository acceptanceRecordRepository;

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
                .status("SUBMITTED")
                .build();

        feedbackRepository.save(feedback);

        notice.setStatus("FEEDBACK_SUBMITTED");
        noticeRepository.save(notice);

        return feedback;
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
