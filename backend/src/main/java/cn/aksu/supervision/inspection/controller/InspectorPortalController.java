package cn.aksu.supervision.inspection.controller;

import cn.aksu.supervision.appeal.entity.Appeal;
import cn.aksu.supervision.appeal.repository.AppealRepository;
import cn.aksu.supervision.appeal.service.AppealService;
import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.dto.EnterpriseDTO;
import cn.aksu.supervision.enterprise.service.EnterpriseService;
import cn.aksu.supervision.report.entity.ComplianceReport;
import cn.aksu.supervision.report.service.ComplianceReportService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 执法端门户查询接口（/api/inspector/**，角色：INSPECTOR / ADMIN）
 * 目的：执法人员不再借用 /api/admin/** 接口，实现服务端角色隔离。
 */
@Tag(name = "执法端-门户查询")
@RestController
@RequestMapping("/api/inspector")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class InspectorPortalController {

    private final EnterpriseService enterpriseService;
    private final AppealRepository appealRepository;
    private final AppealService appealService;
    private final ComplianceReportService reportService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "执法端-企业列表检索（数据权限：仅属地）")
    @GetMapping("/enterprise/list")
    public Result<PageResult<EnterpriseDTO>> listEnterprises(@RequestHeader("Authorization") String auth,
                                                             @RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) String industry,
                                                             @RequestParam(required = false) String area,
                                                             @RequestParam(required = false) Integer status,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(enterpriseService.listEnterprises(keyword, industry, area, status, page, size, userId));
    }

    @Operation(summary = "执法端-企业诉求记录")
    @GetMapping("/appeal/list")
    public Result<PageResult<Appeal>> listAppeals(@RequestParam Long enterpriseId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<Appeal> pageData = (status == null || status.isBlank())
                ? appealRepository.findByEnterpriseIdOrderByCreateTimeDesc(enterpriseId, PageRequest.of(page - 1, size))
                : appealRepository.findByEnterpriseIdAndStatus(enterpriseId, status, PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size));
    }

    @Operation(summary = "执法端-处理诉求（记录操作人）")
    @PutMapping("/appeal/{id}/handle")
    public Result<Appeal> handleAppeal(@PathVariable Long id,
                                       @RequestParam String handleResult,
                                       @RequestHeader("Authorization") String auth) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(appealService.handleAppeal(id, handleResult, null, username));
    }

    @Operation(summary = "执法端-企业合规报告")
    @GetMapping("/report/list")
    public Result<PageResult<ComplianceReport>> listReports(@RequestParam Long enterpriseId,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        if (enterpriseId == null) {
            throw new BusinessException("enterpriseId 不能为空");
        }
        return Result.success(reportService.listByEnterprise(enterpriseId, page, size));
    }

    private Long getUserIdFromToken(String auth) {
        String token = auth.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
