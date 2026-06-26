package cn.aksu.supervision.auth.captcha;

import cn.aksu.supervision.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "验证码管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<CaptchaService.CaptchaResult> getCaptcha() {
        return Result.success(captchaService.generateCaptcha());
    }
}
