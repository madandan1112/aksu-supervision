package cn.aksu.supervision.rectification.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.rectification.entity.RectificationFeedback;
import cn.aksu.supervision.rectification.entity.RectificationNotice;
import cn.aksu.supervision.rectification.repository.RectificationFeedbackRepository;
import cn.aksu.supervision.rectification.repository.RectificationNoticeRepository;
import cn.aksu.supervision.rectification.service.RectificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/rectification")
@RequiredArgsConstructor
public class AdminRectificationController {

    private final RectificationNoticeRepository noticeRepository;
    private final RectificationFeedbackRepository feedbackRepository;
    private final RectificationService rectificationService;

    @GetMapping("/list")
    public Result<PageResult<RectificationNotice>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long enterpriseId) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<RectificationNotice> result;

        if (enterpriseId != null && status != null && !status.isEmpty()) {
            result = noticeRepository.findByEnterpriseIdAndStatusOrderByCreateTimeDesc(enterpriseId, status, pageRequest);
        } else if (enterpriseId != null) {
            result = noticeRepository.findByEnterpriseIdOrderByCreateTimeDesc(enterpriseId, pageRequest);
        } else if (status != null && !status.isEmpty()) {
            result = noticeRepository.findByStatusOrderByCreateTimeDesc(status, pageRequest);
        } else {
            result = noticeRepository.findAllByOrderByCreateTimeDesc(pageRequest);
        }

        return Result.success(PageResult.of(result.getContent(), result.getTotalElements(), page, size));
    }

    @GetMapping("/{id:\\d+}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        return Result.success(rectificationService.getNoticeDetail(id));
    }

    @GetMapping("/{id:\\d+}/feedbacks")
    public Result<List<RectificationFeedback>> getFeedbacks(@PathVariable Long id) {
        return Result.success(feedbackRepository.findByNoticeIdOrderByCreateTimeDesc(id));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", noticeRepository.count());
        long issued = noticeRepository.findByStatusOrderByCreateTimeDesc("ISSUED", PageRequest.of(0, 1)).getTotalElements();
        long completed = noticeRepository.countByStatusIn(java.util.List.of("ACCEPTED", "COMPLETED"));
        stats.put("issued", issued);
        stats.put("completed", completed);
        return Result.success(stats);
    }
}
