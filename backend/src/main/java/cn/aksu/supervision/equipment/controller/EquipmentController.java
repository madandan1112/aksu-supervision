package cn.aksu.supervision.equipment.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.equipment.dto.EquipmentAddRequest;
import cn.aksu.supervision.equipment.dto.EquipmentReportRequest;
import cn.aksu.supervision.equipment.entity.EquipmentLedger;
import cn.aksu.supervision.equipment.entity.EquipmentReport;
import cn.aksu.supervision.equipment.service.EquipmentService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "设备监测管理")
@RestController
@RequestMapping("/api/enterprise/equipment")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EnterpriseRepository enterpriseRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "添加设备台账")
    @PostMapping
    public Result<EquipmentLedger> addEquipment(@RequestHeader("Authorization") String auth,
                                                 @Valid @RequestBody EquipmentAddRequest request) {
        Long enterpriseId = getEnterpriseId(auth);
        return Result.success(equipmentService.addEquipment(enterpriseId, request));
    }

    @Operation(summary = "设备列表")
    @GetMapping("/list")
    public Result<PageResult<EquipmentLedger>> listEquipment(@RequestHeader("Authorization") String auth,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Long enterpriseId = getEnterpriseId(auth);
        return Result.success(equipmentService.listByEnterprise(enterpriseId, page, size));
    }

    @Operation(summary = "编辑设备")
    @PutMapping("/{id}")
    public Result<EquipmentLedger> updateEquipment(@PathVariable Long id,
                                                    @Valid @RequestBody EquipmentAddRequest request) {
        return Result.success(equipmentService.updateEquipment(id, request));
    }

    @Operation(summary = "设备状态上报")
    @PostMapping("/{id}/report")
    public Result<EquipmentReport> reportStatus(@RequestHeader("Authorization") String auth,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody EquipmentReportRequest request) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(equipmentService.reportStatus(id, request, username));
    }

    @Operation(summary = "上报记录")
    @GetMapping("/{id}/reports")
    public Result<List<EquipmentReport>> getReports(@PathVariable Long id) {
        return Result.success(equipmentService.getReports(id));
    }

    private Long getEnterpriseId(String auth) {
        String token = auth.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new cn.aksu.supervision.common.BusinessException("企业信息不存在"));
        return enterprise.getId();
    }
}
