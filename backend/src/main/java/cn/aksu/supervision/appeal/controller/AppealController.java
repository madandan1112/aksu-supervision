package cn.aksu.supervision.appeal.controller;

import cn.aksu.supervision.appeal.dto.AppealCreateRequest;
import cn.aksu.supervision.appeal.dto.AppealQueryRequest;
import cn.aksu.supervision.appeal.entity.Appeal;
import cn.aksu.supervision.appeal.service.AppealService;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "诉求管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class AppealController {

    private final AppealService appealService;
    private final EnterpriseRepository enterpriseRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "企业提交诉求")
    @PostMapping("/appeal")
    public Result<Appeal> createAppeal(@RequestHeader("Authorization") String auth,
                                       @Valid @RequestBody AppealCreateRequest request) {
        Long userId = getUserIdFromToken(auth);
        Long enterpriseId = getEnterpriseIdByUserId(userId);
        return Result.success(appealService.createAppeal(enterpriseId, request));
    }

    @Operation(summary = "企业查看自己的诉求")
    @GetMapping("/appeal/list")
    public Result<PageResult<Appeal>> listMyAppeals(@RequestHeader("Authorization") String auth,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserIdFromToken(auth);
        Long enterpriseId = getEnterpriseIdByUserId(userId);
        return Result.success(appealService.listByEnterprise(enterpriseId, page, size));
    }

    @Operation(summary = "诉求详情")
    @GetMapping("/appeal/{id}")
    public Result<Appeal> getAppealDetail(@PathVariable Long id) {
        return Result.success(appealService.getAppealDetail(id));
    }

    @Operation(summary = "评价诉求")
    @PostMapping("/appeal/{id}/evaluate")
    public Result<Appeal> evaluateAppeal(@RequestHeader("Authorization") String auth,
                                          @PathVariable Long id,
                                          @RequestParam Integer satisfaction,
                                          @RequestParam(required = false) String comment) {
        Long userId = getUserIdFromToken(auth);
        Long enterpriseId = getEnterpriseIdByUserId(userId);
        return Result.success(appealService.evaluateAppeal(id, enterpriseId, satisfaction, comment));
    }

    @Operation(summary = "管理端-诉求列表")
    @GetMapping("/admin/appeal/list")
    public Result<PageResult<Appeal>> listAppeals(AppealQueryRequest request) {
        return Result.success(appealService.listForAdmin(request.getStatus(), request.getType(),
                request.getKeyword(), request.getPage(), request.getSize()));
    }

    @Operation(summary = "管理端-分配诉求")
    @PutMapping("/admin/appeal/{id}/assign")
    public Result<Appeal> assignAppeal(@PathVariable Long id,
                                        @RequestParam String assignedTo,
                                        @RequestHeader("Authorization") String auth) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(appealService.assignAppeal(id, assignedTo, username));
    }

    @Operation(summary = "管理端-处理诉求")
    @PutMapping("/admin/appeal/{id}/handle")
    public Result<Appeal> handleAppeal(@PathVariable Long id,
                                        @RequestParam String handleResult,
                                        @RequestParam(required = false) String handleAttachment,
                                        @RequestHeader("Authorization") String auth) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(appealService.handleAppeal(id, handleResult, handleAttachment, username));
    }

    @Operation(summary = "管理端-诉求统计")
    @GetMapping("/admin/appeal/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(appealService.getStatistics());
    }

    private Long getUserIdFromToken(String auth) {
        String token = auth.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    private Long getEnterpriseIdByUserId(Long userId) {
        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new cn.aksu.supervision.common.BusinessException("企业信息不存在"));
        return enterprise.getId();
    }
}
