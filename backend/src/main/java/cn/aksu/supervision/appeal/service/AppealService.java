package cn.aksu.supervision.appeal.service;

import cn.aksu.supervision.appeal.dto.AppealCreateRequest;
import cn.aksu.supervision.appeal.dto.AppealQueryRequest;
import cn.aksu.supervision.appeal.entity.Appeal;
import cn.aksu.supervision.appeal.entity.AppealProcessLog;
import cn.aksu.supervision.appeal.repository.AppealProcessLogRepository;
import cn.aksu.supervision.appeal.repository.AppealRepository;
import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AppealService {

    private final AppealRepository appealRepository;
    private final AppealProcessLogRepository processLogRepository;

    public Appeal createAppeal(Long enterpriseId, AppealCreateRequest request) {
        Appeal appeal = Appeal.builder()
                .enterpriseId(enterpriseId)
                .appealType(request.getAppealType())
                .title(request.getTitle())
                .content(request.getContent())
                .relatedFields(request.getRelatedFields())
                .attachments(request.getAttachments())
                .status("PENDING")
                .build();

        appealRepository.save(appeal);

        AppealProcessLog log = AppealProcessLog.builder()
                .appealId(appeal.getId())
                .action("CREATE")
                .remark("企业提交诉求")
                .build();
        processLogRepository.save(log);

        return appeal;
    }

    @Transactional(readOnly = true)
    public PageResult<Appeal> listByEnterprise(Long enterpriseId, int page, int size) {
        Page<Appeal> pageData = appealRepository.findByEnterpriseIdOrderByCreateTimeDesc(
                enterpriseId, PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public Appeal getAppealDetail(Long id) {
        return appealRepository.findById(id)
                .orElseThrow(() -> new BusinessException("诉求不存在"));
    }

    public Appeal evaluateAppeal(Long id, Long enterpriseId, Integer satisfaction, String comment) {
        Appeal appeal = appealRepository.findById(id)
                .orElseThrow(() -> new BusinessException("诉求不存在"));

        if (!appeal.getEnterpriseId().equals(enterpriseId)) {
            throw new BusinessException("无权评价此诉求");
        }

        if (!"HANDLED".equals(appeal.getStatus())) {
            throw new BusinessException("只有已处理的诉求才能评价");
        }

        appeal.setSatisfaction(satisfaction);
        appeal.setEvaluateComment(comment);
        appeal.setEvaluateTime(LocalDateTime.now());
        appeal.setStatus("EVALUATED");

        appealRepository.save(appeal);

        AppealProcessLog log = AppealProcessLog.builder()
                .appealId(id)
                .action("EVALUATE")
                .remark("企业评价满意度: " + satisfaction + "分")
                .build();
        processLogRepository.save(log);

        return appeal;
    }

    @Transactional(readOnly = true)
    public PageResult<Appeal> listForAdmin(String status, String type, String keyword, int page, int size) {
        Page<Appeal> pageData = appealRepository.findByConditions(status, type, keyword,
                PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public Appeal assignAppeal(Long id, String assignedTo, String operator) {
        Appeal appeal = appealRepository.findById(id)
                .orElseThrow(() -> new BusinessException("诉求不存在"));

        appeal.setAssignedTo(assignedTo);
        appeal.setStatus("ASSIGNED");

        appealRepository.save(appeal);

        AppealProcessLog log = AppealProcessLog.builder()
                .appealId(id)
                .action("ASSIGN")
                .operator(operator)
                .remark("分配给: " + assignedTo)
                .build();
        processLogRepository.save(log);

        return appeal;
    }

    public Appeal handleAppeal(Long id, String handleResult, String handleAttachment, String operator) {
        Appeal appeal = appealRepository.findById(id)
                .orElseThrow(() -> new BusinessException("诉求不存在"));

        appeal.setHandleResult(handleResult);
        appeal.setHandleAttachment(handleAttachment);
        appeal.setHandleTime(LocalDateTime.now());
        appeal.setStatus("HANDLED");

        appealRepository.save(appeal);

        AppealProcessLog log = AppealProcessLog.builder()
                .appealId(id)
                .action("HANDLE")
                .operator(operator)
                .remark("处理诉求")
                .build();
        processLogRepository.save(log);

        return appeal;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", appealRepository.count());
        stats.put("pending", appealRepository.countByStatus("PENDING"));
        stats.put("assigned", appealRepository.countByStatus("ASSIGNED"));
        stats.put("handled", appealRepository.countByStatus("HANDLED"));
        stats.put("evaluated", appealRepository.countByStatus("EVALUATED"));
        stats.put("satisfied", appealRepository.countSatisfied());
        return stats;
    }
}
