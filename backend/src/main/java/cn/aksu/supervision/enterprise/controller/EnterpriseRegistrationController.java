package cn.aksu.supervision.enterprise.controller;

import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.entity.EnterpriseRegistration;
import cn.aksu.supervision.enterprise.service.EnterpriseRegistrationService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "企业注册")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class EnterpriseRegistrationController {

    private final EnterpriseRegistrationService registrationService;
    private final JwtTokenProvider jwtTokenProvider;

    // ========== 小程序端：企业注册流程 ==========

    @Operation(summary = "Step1: 上传营业执照+OCR识别")
    @PostMapping("/enterprise/register/license")
    public Result<Map<String, Object>> uploadLicense(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(registrationService.uploadAndOcrLicense(userId, file));
    }

    @Operation(summary = "Step2: 填写企业基本信息+选择行业分类")
    @PostMapping("/enterprise/register/info")
    public Result<EnterpriseRegistration> fillInfo(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> info) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(registrationService.fillEnterpriseInfo(userId, info));
    }

    @Operation(summary = "Step3: 上传资质证照")
    @PostMapping("/enterprise/register/qualification")
    public Result<EnterpriseRegistration> uploadQualification(
            @RequestHeader("Authorization") String auth,
            @RequestParam("files") List<MultipartFile> files) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(registrationService.uploadQualifications(userId, files));
    }

    @Operation(summary = "Step4: 上传门店照片（门头照+店内照）")
    @PostMapping("/enterprise/register/photos")
    public Result<EnterpriseRegistration> uploadStorePhotos(
            @RequestHeader("Authorization") String auth,
            @RequestParam(value = "storefrontPhoto", required = false) MultipartFile storefrontPhoto,
            @RequestParam(value = "interiorPhoto", required = false) MultipartFile interiorPhoto) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(registrationService.uploadStorePhotos(userId, storefrontPhoto, interiorPhoto));
    }

    @Operation(summary = "Step5: 提交注册申请")
    @PostMapping("/enterprise/register/submit")
    public Result<EnterpriseRegistration> submitRegistration(
            @RequestHeader("Authorization") String auth) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(registrationService.submitRegistration(userId));
    }

    @Operation(summary = "查询注册状态")
    @GetMapping("/enterprise/register/status")
    public Result<Map<String, Object>> getRegistrationStatus(
            @RequestHeader("Authorization") String auth) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(registrationService.getRegistrationStatus(userId));
    }

    // ========== 管理端：注册审核 ==========

    @Operation(summary = "待审核注册列表")
    @GetMapping("/admin/enterprise-registration/pending")
    public Result<List<EnterpriseRegistration>> listPending() {
        return Result.success(registrationService.listPendingRegistrations());
    }

    @Operation(summary = "所有注册列表")
    @GetMapping("/admin/enterprise-registration/list")
    public Result<List<EnterpriseRegistration>> listAll() {
        return Result.success(registrationService.listAllRegistrations());
    }

    @Operation(summary = "审核通过")
    @PostMapping("/admin/enterprise-registration/{id}/approve")
    public Result<Enterprise> approveRegistration(
            @PathVariable Long id,
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String comment) {
        Long reviewerId = getUserIdFromToken(auth);
        return Result.success(registrationService.approveRegistration(id, reviewerId, comment));
    }

    @Operation(summary = "审核拒绝")
    @PostMapping("/admin/enterprise-registration/{id}/reject")
    public Result<Void> rejectRegistration(
            @PathVariable Long id,
            @RequestHeader("Authorization") String auth,
            @RequestParam String comment) {
        Long reviewerId = getUserIdFromToken(auth);
        registrationService.rejectRegistration(id, reviewerId, comment);
        return Result.success();
    }

    private Long getUserIdFromToken(String auth) {
        String token = auth.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
