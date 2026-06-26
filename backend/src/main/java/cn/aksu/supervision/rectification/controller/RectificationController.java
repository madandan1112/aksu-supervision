package cn.aksu.supervision.rectification.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.rectification.dto.AcceptanceRequest;
import cn.aksu.supervision.rectification.dto.FeedbackSubmitRequest;
import cn.aksu.supervision.rectification.entity.AcceptanceRecord;
import cn.aksu.supervision.rectification.entity.RectificationFeedback;
import cn.aksu.supervision.rectification.entity.RectificationNotice;
import cn.aksu.supervision.rectification.service.RectificationService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "整改管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class RectificationController {

    private final RectificationService rectificationService;
    private final EnterpriseRepository enterpriseRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "企业整改列表")
    @GetMapping("/enterprise/rectification/list")
    public Result<PageResult<RectificationNotice>> listByEnterprise(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserIdFromToken(auth);
        Long enterpriseId = getEnterpriseIdByUserId(userId);
        return Result.success(rectificationService.listByEnterprise(enterpriseId, page, size));
    }

    @Operation(summary = "整改详情")
    @GetMapping("/enterprise/rectification/{id}")
    public Result<Map<String, Object>> getNoticeDetail(@PathVariable Long id) {
        return Result.success(rectificationService.getNoticeDetail(id));
    }

    @Operation(summary = "提交整改反馈")
    @PostMapping("/enterprise/rectification/{id}/feedback")
    public Result<RectificationFeedback> submitFeedback(@RequestHeader("Authorization") String auth,
                                                         @PathVariable Long id,
                                                         @Valid @RequestBody FeedbackSubmitRequest request) {
        Long userId = getUserIdFromToken(auth);
        Long enterpriseId = getEnterpriseIdByUserId(userId);
        return Result.success(rectificationService.submitFeedback(id, enterpriseId, request));
    }

    @Operation(summary = "待验收列表")
    @GetMapping("/inspector/rectification/pending")
    public Result<PageResult<RectificationNotice>> listPendingAcceptance(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(rectificationService.listPendingAcceptance(userId, page, size));
    }

    @Operation(summary = "验收操作")
    @PostMapping("/inspector/rectification/{id}/accept")
    public Result<AcceptanceRecord> accept(@RequestHeader("Authorization") String auth,
                                            @PathVariable Long id,
                                            @Valid @RequestBody AcceptanceRequest request) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(rectificationService.accept(id, userId, request));
    }

    @Operation(summary = "AI辅助比对")
    @GetMapping("/inspector/rectification/{id}/compare")
    public Result<Map<String, Object>> compareForAI(@PathVariable Long id) {
        return Result.success(rectificationService.compareForAI(id));
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
