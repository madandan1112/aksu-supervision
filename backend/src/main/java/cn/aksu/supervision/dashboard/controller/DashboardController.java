package cn.aksu.supervision.dashboard.controller;

import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.dashboard.dto.DashboardStats;
import cn.aksu.supervision.dashboard.service.DashboardService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "总览数据（按用户权限过滤）")
    @GetMapping("/overview")
    public Result<DashboardStats> getOverview(HttpServletRequest request) {
        Long userId = getUserId(request);
        String userType = getUserType(request);
        return Result.success(dashboardService.getOverview(userId, userType));
    }

    @Operation(summary = "行业视图")
    @GetMapping("/industry-view")
    public Result<Map<String, Object>> getIndustryView(HttpServletRequest request) {
        Long userId = getUserId(request);
        String userType = getUserType(request);
        return Result.success(dashboardService.getIndustryView(userId, userType));
    }

    @Operation(summary = "区域视图")
    @GetMapping("/area-view")
    public Result<Map<String, Object>> getAreaView(HttpServletRequest request) {
        Long userId = getUserId(request);
        String userType = getUserType(request);
        return Result.success(dashboardService.getAreaView(userId, userType));
    }

    @Operation(summary = "风险画像")
    @GetMapping("/risk-profile")
    public Result<Map<String, Object>> getRiskProfile(HttpServletRequest request) {
        Long userId = getUserId(request);
        String userType = getUserType(request);
        return Result.success(dashboardService.getRiskProfile(userId, userType));
    }

    private Long getUserId(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null) {
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        return null;
    }

    private String getUserType(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null) {
            return jwtTokenProvider.getUserTypeFromToken(token);
        }
        return "admin";
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
