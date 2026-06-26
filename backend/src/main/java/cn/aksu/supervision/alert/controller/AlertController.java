package cn.aksu.supervision.alert.controller;

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
    @GetMapping("/{id}")
    public Result<Alert> getAlertDetail(@PathVariable Long id) {
        return Result.success(alertService.getAlertDetail(id));
    }

    @Operation(summary = "处理预警")
    @PostMapping("/{id}/handle")
    public Result<Alert> handleAlert(@RequestHeader("Authorization") String auth,
                                      @PathVariable Long id,
                                      @RequestParam String handleResult) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(alertService.handleAlert(id, handleResult, username));
    }

    @Operation(summary = "预警统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(alertService.getStatistics());
    }
}
