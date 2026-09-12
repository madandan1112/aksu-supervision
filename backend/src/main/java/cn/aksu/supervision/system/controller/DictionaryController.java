package cn.aksu.supervision.system.controller;

import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.system.entity.EnterpriseType;
import cn.aksu.supervision.system.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用数据字典（登录即可访问，供企业注册/编辑等行业分类下拉使用，
 * 替代此前企业端借用 /api/admin/system/enterprise-type/active 的做法）。
 */
@Tag(name = "数据字典")
@RestController
@RequestMapping("/api/dictionary")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class DictionaryController {

    private final SystemService systemService;

    @Operation(summary = "启用的行业分类列表")
    @GetMapping("/enterprise-type/active")
    public Result<List<EnterpriseType>> activeEnterpriseTypes() {
        return Result.success(systemService.getActiveEnterpriseTypes());
    }
}
