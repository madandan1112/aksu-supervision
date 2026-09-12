package cn.aksu.supervision.inspection.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.inspection.entity.InspectionRecord;
import cn.aksu.supervision.inspection.repository.InspectionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/inspection")
@RequiredArgsConstructor
public class AdminInspectionController {

    private final InspectionRecordRepository inspectionRecordRepository;

    @GetMapping("/list")
    public Result<PageResult<InspectionRecord>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long enterpriseId) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<InspectionRecord> result;

        if (enterpriseId != null) {
            result = inspectionRecordRepository.findByEnterpriseIdOrderByCreateTimeDesc(enterpriseId, pageRequest);
        } else if (status != null && !status.isEmpty()) {
            result = inspectionRecordRepository.findByStatusOrderByCreateTimeDesc(status, pageRequest);
        } else {
            result = inspectionRecordRepository.findAllByOrderByCreateTimeDesc(pageRequest);
        }

        return Result.success(PageResult.of(result.getContent(), result.getTotalElements(), page, size));
    }

    @GetMapping("/{id:\\d+}")
    public Result<InspectionRecord> getDetail(@PathVariable Long id) {
        return Result.success(inspectionRecordRepository.findById(id).orElse(null));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", inspectionRecordRepository.count());
        return Result.success(stats);
    }
}
