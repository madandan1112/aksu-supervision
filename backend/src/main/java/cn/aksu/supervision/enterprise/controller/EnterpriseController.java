package cn.aksu.supervision.enterprise.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.dto.ContactRequest;
import cn.aksu.supervision.enterprise.dto.EnterpriseDTO;
import cn.aksu.supervision.enterprise.dto.EnterpriseUpdateRequest;
import cn.aksu.supervision.enterprise.entity.EnterpriseContact;
import cn.aksu.supervision.enterprise.service.EnterpriseService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "企业管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class EnterpriseController {

    private final EnterpriseService enterpriseService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "获取当前企业信息")
    @GetMapping("/enterprise/profile")
    public Result<EnterpriseDTO> getProfile(@RequestHeader("Authorization") String auth) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(enterpriseService.getEnterpriseByUserId(userId));
    }

    @Operation(summary = "更新企业信息")
    @PutMapping("/enterprise/profile")
    public Result<EnterpriseDTO> updateProfile(@RequestHeader("Authorization") String auth,
                                                @RequestBody EnterpriseUpdateRequest request) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(enterpriseService.updateEnterprise(userId, request));
    }

    @Operation(summary = "添加联系人")
    @PostMapping("/enterprise/contacts")
    public Result<EnterpriseContact> addContact(@RequestHeader("Authorization") String auth,
                                                 @Valid @RequestBody ContactRequest request) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(enterpriseService.addContact(userId, request));
    }

    @Operation(summary = "修改联系人")
    @PutMapping("/enterprise/contacts/{id}")
    public Result<EnterpriseContact> updateContact(@RequestHeader("Authorization") String auth,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody ContactRequest request) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(enterpriseService.updateContact(id, userId, request));
    }

    @Operation(summary = "获取联系人列表")
    @GetMapping("/enterprise/contacts")
    public Result<List<EnterpriseContact>> getContacts(@RequestHeader("Authorization") String auth) {
        Long userId = getUserIdFromToken(auth);
        return Result.success(enterpriseService.getContacts(userId));
    }

    @Operation(summary = "管理端-企业列表")
    @GetMapping("/admin/enterprise/list")
    public Result<PageResult<EnterpriseDTO>> listEnterprises(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(enterpriseService.listEnterprises(keyword, industry, area, status, page, size));
    }

    @Operation(summary = "扫码查询企业信息")
    @GetMapping("/enterprise/scan")
    public Result<EnterpriseDTO> scanQuery(@RequestParam String code) {
        return Result.success(enterpriseService.scanQuery(code));
    }

    @Operation(summary = "管理端-企业详情")
    @GetMapping("/admin/enterprise/{id}")
    public Result<EnterpriseDTO> getEnterpriseDetail(@PathVariable Long id) {
        return Result.success(enterpriseService.getEnterpriseById(id));
    }

    @Operation(summary = "管理端-更新企业状态")
    @PutMapping("/admin/enterprise/{id}/status")
    public Result<Void> updateEnterpriseStatus(@PathVariable Long id,
                                                @RequestParam Integer status) {
        enterpriseService.updateEnterpriseStatus(id, status);
        return Result.success();
    }

    private Long getUserIdFromToken(String auth) {
        String token = auth.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
