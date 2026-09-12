package cn.aksu.supervision.auth;

import cn.aksu.supervision.auth.captcha.CaptchaService;
import cn.aksu.supervision.auth.dto.LoginRequest;
import cn.aksu.supervision.auth.dto.RegisterRequest;
import cn.aksu.supervision.auth.dto.TokenResponse;
import cn.aksu.supervision.auth.dto.WxLoginRequest;
import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.security.JwtTokenProvider;
import cn.aksu.supervision.system.entity.SysUser;
import cn.aksu.supervision.system.repository.SysUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CaptchaService captchaService;
    private final cn.aksu.supervision.enterprise.service.EnterpriseService enterpriseService;

    @Operation(summary = "用户名密码登录")
    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        if (!captchaService.validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        SysUser user = sysUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getUserType());

        TokenResponse response = TokenResponse.builder()
                .token(token)
                .expiresIn(jwtTokenProvider.getExpiration() / 1000)
                .userType(user.getUserType())
                .realName(user.getRealName())
                .build();

        return Result.success(response);
    }

    @Operation(summary = "微信登录")
    @PostMapping("/wx-login")
    public Result<TokenResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        // 微信登录：通过code换取openid，此处简化处理
        // 实际应调用微信API: https://api.weixin.qq.com/sns/jscode2session
        String openid = request.getCode(); // 占位，实际应从微信API获取

        SysUser user = sysUserRepository.findByWxOpenid(openid)
                .orElseThrow(() -> new BusinessException(404, "该微信未绑定账号，请先注册"));

        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getUserType());

        TokenResponse response = TokenResponse.builder()
                .token(token)
                .expiresIn(jwtTokenProvider.getExpiration() / 1000)
                .userType(user.getUserType())
                .realName(user.getRealName())
                .build();

        return Result.success(response);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (sysUserRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        if (sysUserRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已注册");
        }

        SysUser user = SysUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .realName(request.getRealName())
                .phone(request.getPhone())
                .userType(request.getUserType())
                .status(1)
                .build();

        sysUserRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getUserType());

        TokenResponse response = TokenResponse.builder()
                .token(token)
                .expiresIn(jwtTokenProvider.getExpiration() / 1000)
                .userType(user.getUserType())
                .realName(user.getRealName())
                .build();

        return Result.success(response);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/userinfo")
    public Result<Map<String, Object>> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException(401, "未登录");
        }

        String username = auth.getName();
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        Map<String, Object> userInfo = new java.util.HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName() != null ? user.getRealName() : "");
        userInfo.put("userType", user.getUserType() != null ? user.getUserType() : "");
        userInfo.put("phone", user.getPhone() != null ? user.getPhone() : "");
        userInfo.put("email", user.getEmail() != null ? user.getEmail() : "");
        userInfo.put("orgName", user.getOrgName() != null ? user.getOrgName() : "");

        // 企业用户附带所属企业名（首页头部展示）；执法用户附带属地
        if ("enterprise".equals(user.getUserType()) || "enterprise_user".equals(user.getUserType())) {
            try {
                cn.aksu.supervision.enterprise.dto.EnterpriseDTO ent =
                        enterpriseService.getEnterpriseByUserId(user.getId());
                userInfo.put("enterpriseName", ent != null ? ent.getName() : "");
                userInfo.put("enterpriseId", ent != null ? ent.getId() : null);
            } catch (Exception e) {
                userInfo.put("enterpriseName", "");
            }
        }

        return Result.success(userInfo);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException(401, "未登录");
        }

        String username = auth.getName();
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            throw new BusinessException(400, "旧密码和新密码不能为空");
        }

        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserRepository.save(user);

        return Result.success(null);
    }

    @Operation(summary = "小程序登录（免验证码）")
    @PostMapping("/miniprogram-login")
    public Result<TokenResponse> miniprogramLogin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || username.isBlank()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException(400, "密码不能为空");
        }

        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getUserType());

        TokenResponse response = TokenResponse.builder()
                .token(token)
                .expiresIn(jwtTokenProvider.getExpiration() / 1000)
                .userType(user.getUserType())
                .realName(user.getRealName())
                .build();

        return Result.success(response);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT是无状态的，客户端清除token即可
        // 如需服务端失效，可配合Redis黑名单实现
        return Result.success(null);
    }
}
