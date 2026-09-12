package cn.aksu.supervision.alert.controller;

import cn.aksu.supervision.alert.constants.AlertConstants;
import cn.aksu.supervision.alert.entity.Alert;
import cn.aksu.supervision.alert.service.AlertService;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "预警管理")
@RestController
@RequestMapping("/api/inspector/alert")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class AlertController {

    private final AlertService alertService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "预警列表")
    @GetMapping("/list")
    public Result<PageResult<Alert>> listAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(alertService.listAlerts(status, level, type, page, size));
    }

    @Operation(summary = "预警详情")
    @GetMapping("/{id:\\d+}")
    public Result<Alert> getAlertDetail(@PathVariable Long id) {
        return Result.success(alertService.getAlertDetail(id));
    }

    @Operation(summary = "接收预警（开始处理）")
    @PostMapping("/{id}/accept")
    public Result<Alert> acceptAlert(@RequestHeader("Authorization") String auth,
                                      @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(alertService.acceptAlert(id, userId));
    }

    @Operation(summary = "处理预警（闭环关闭）")
    @PostMapping("/{id}/handle")
    public Result<Alert> handleAlert(@RequestHeader("Authorization") String auth,
                                      @PathVariable Long id,
                                      @RequestParam String handleResult) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(alertService.handleAlert(id, handleResult, username));
    }

    @Operation(summary = "退回预警（重新分配）")
    @PostMapping("/{id}/reject")
    public Result<Alert> rejectAlert(@PathVariable Long id,
                                      @RequestParam String reason) {
        return Result.success(alertService.rejectAlert(id, reason));
    }

    @Operation(summary = "预警统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(alertService.getStatistics());
    }

    @Operation(summary = "手动触发预警扫描（管理端）")
    @PostMapping("/scan")
    public Result<Map<String, Object>> triggerScan() {
        int count = alertService.triggerManualScan();
        return Result.success(Map.of("newAlerts", count, "message", "扫描完成，新增" + count + "条预警"));
    }

    @Operation(summary = "企业活跃预警列表")
    @GetMapping("/enterprise/{enterpriseId}/active")
    public Result<List<Alert>> getEnterpriseActiveAlerts(@PathVariable Long enterpriseId) {
        return Result.success(alertService.getEnterpriseActiveAlerts(enterpriseId));
    }

    @Operation(summary = "预警类型选项（前端下拉）")
    @GetMapping("/types")
    public Result<List<Map<String, String>>> getAlertTypes() {
        return Result.success(List.of(
                Map.of("value", AlertConstants.TYPE_LICENSE_EXPIRING, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_LICENSE_EXPIRING)),
                Map.of("value", AlertConstants.TYPE_LICENSE_EXPIRED, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_LICENSE_EXPIRED)),
                Map.of("value", AlertConstants.TYPE_RECTIFICATION_OVERDUE, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_RECTIFICATION_OVERDUE)),
                Map.of("value", AlertConstants.TYPE_INSPECTION_OVERDUE, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_INSPECTION_OVERDUE)),
                Map.of("value", AlertConstants.TYPE_REPORT_MISSING, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_REPORT_MISSING)),
                Map.of("value", AlertConstants.TYPE_REGISTRATION_ANOMALY, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_REGISTRATION_ANOMALY)),
                Map.of("value", AlertConstants.TYPE_CREDIT_ANOMALY, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_CREDIT_ANOMALY)),
                Map.of("value", AlertConstants.TYPE_REPEAT_VIOLATION, "label", AlertConstants.getTypeLabel(AlertConstants.TYPE_REPEAT_VIOLATION))
        ));
    }
}
