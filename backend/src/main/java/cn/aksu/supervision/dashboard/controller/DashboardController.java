package cn.aksu.supervision.dashboard.controller;

import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.dashboard.dto.DashboardStats;
import cn.aksu.supervision.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "数据可视化")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "总览数据")
    @GetMapping("/overview")
    public Result<DashboardStats> getOverview() {
        return Result.success(dashboardService.getOverview());
    }

    @Operation(summary = "行业视图")
    @GetMapping("/industry-view")
    public Result<Map<String, Object>> getIndustryView() {
        return Result.success(dashboardService.getIndustryView());
    }

    @Operation(summary = "区域视图")
    @GetMapping("/area-view")
    public Result<Map<String, Object>> getAreaView() {
        return Result.success(dashboardService.getAreaView());
    }

    @Operation(summary = "风险画像")
    @GetMapping("/risk-profile")
    public Result<Map<String, Object>> getRiskProfile() {
        return Result.success(dashboardService.getRiskProfile());
    }
}
