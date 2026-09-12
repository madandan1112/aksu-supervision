package cn.aksu.supervision.inspection.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.inspection.dto.InspectionSubmitRequest;
import cn.aksu.supervision.inspection.entity.InspectionRecord;
import cn.aksu.supervision.inspection.service.InspectionService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "现场检查管理")
@RestController
@RequestMapping("/api/inspector/inspection")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class InspectionController {

    private final InspectionService inspectionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "提交检查记录")
    @PostMapping("/submit")
    public Result<InspectionRecord> submitRecord(@RequestHeader("Authorization") String auth,
                                                  @Valid @RequestBody InspectionSubmitRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(inspectionService.submitRecord(userId, request));
    }

    @Operation(summary = "检查记录列表")
    @GetMapping("/list")
    public Result<PageResult<InspectionRecord>> listRecords(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(inspectionService.listByInspector(userId, page, size));
    }

    @Operation(summary = "检查详情")
    @GetMapping("/{id:\\d+}")
    public Result<InspectionRecord> getRecordDetail(@PathVariable Long id) {
        return Result.success(inspectionService.getRecordDetail(id));
    }

    @Operation(summary = "生成整改通知书")
    @PostMapping("/{id}/generate-notice")
    public Result<InspectionRecord> generateNotice(@PathVariable Long id) {
        return Result.success(inspectionService.generateNotice(id));
    }
}
