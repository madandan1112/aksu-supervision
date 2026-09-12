package cn.aksu.supervision.report.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.report.dto.ReportUploadRequest;
import cn.aksu.supervision.report.entity.ComplianceReport;
import cn.aksu.supervision.report.service.ComplianceReportService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "合规报告管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class ComplianceReportController {

    private final ComplianceReportService reportService;
    private final EnterpriseRepository enterpriseRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "上传报告")
    @PostMapping("/enterprise/report/upload")
    public Result<ComplianceReport> uploadReport(@RequestHeader("Authorization") String auth,
                                                  @Valid @RequestBody ReportUploadRequest request) {
        String token = auth.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new cn.aksu.supervision.common.BusinessException("企业信息不存在"));

        return Result.success(reportService.uploadReport(enterprise.getId(), request, username));
    }

    @Operation(summary = "企业报告列表")
    @GetMapping("/enterprise/report/list")
    public Result<PageResult<ComplianceReport>> listByEnterprise(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new cn.aksu.supervision.common.BusinessException("企业信息不存在"));
        return Result.success(reportService.listByEnterprise(enterprise.getId(), page, size));
    }

    @Operation(summary = "管理端报告列表")
    @GetMapping("/admin/report/list")
    public Result<PageResult<ComplianceReport>> listForAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reportService.listForAdmin(status, keyword, page, size));
    }

    @Operation(summary = "审核报告")
    @PutMapping("/admin/report/{id}/review")
    public Result<ComplianceReport> reviewReport(@PathVariable Long id,
                                                   @RequestParam String status,
                                                   @RequestParam(required = false) String comment,
                                                   @RequestHeader("Authorization") String auth) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(reportService.reviewReport(id, status, comment, username));
    }

    @Operation(summary = "即将到期报告")
    @GetMapping("/admin/report/expiring")
    public Result<List<ComplianceReport>> getExpiringReports() {
        return Result.success(reportService.getExpiringReports());
    }

    @Operation(summary = "更新报告（重新上传）")
    @PutMapping("/admin/report/{id}")
    public Result<ComplianceReport> updateReport(@PathVariable Long id,
                                                   @Valid @RequestBody ReportUploadRequest request) {
        return Result.success(reportService.updateReport(id, request));
    }

    @Operation(summary = "报告详情")
    @GetMapping("/admin/report/{id}")
    public Result<ComplianceReport> getReport(@PathVariable Long id) {
        return Result.success(reportService.getReport(id));
    }
}
