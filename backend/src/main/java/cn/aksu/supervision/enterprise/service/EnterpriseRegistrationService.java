package cn.aksu.supervision.enterprise.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.entity.EnterpriseRegistration;
import cn.aksu.supervision.enterprise.repository.EnterpriseRegistrationRepository;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.ocr.service.OcrService;
import cn.aksu.supervision.system.entity.SysRole;
import cn.aksu.supervision.system.entity.SysUser;
import cn.aksu.supervision.system.repository.SysRoleRepository;
import cn.aksu.supervision.system.repository.SysUserRepository;
import cn.aksu.supervision.file.service.MinioService;
import cn.aksu.supervision.alert.listener.RegistrationRejectedEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseRegistrationService {

    /** 审核通过后新建企业账号的默认密码（首次登录后应引导修改） */
    private static final String DEFAULT_ENTERPRISE_PASSWORD = "123456";
    private static final String ENTERPRISE_ROLE_CODE = "ENTERPRISE_USER";

    private final EnterpriseRegistrationRepository registrationRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final SysUserRepository sysUserRepository;
    private final SysRoleRepository sysRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OcrService ocrService;
    private final MinioService minioService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * Step 1: Upload business license and OCR recognize
     */
    @Transactional
    public Map<String, Object> uploadAndOcrLicense(Long userId, MultipartFile file) {
        // Upload to MinIO
        String licenseUrl = minioService.uploadFile(file, "license");

        // OCR recognize
        Map<String, Object> ocrResult = new HashMap<>();
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            ocrResult = ocrService.recognize(base64, "business_license");
        } catch (Exception e) {
            log.warn("OCR failed, returning license URL only: {}", e.getMessage());
        }

        // Create or update registration
        EnterpriseRegistration reg = registrationRepository
                .findByUserIdAndStatus(userId, "DRAFT")
                .orElse(EnterpriseRegistration.builder()
                        .userId(userId)
                        .step(1)
                        .status("DRAFT")
                        .build());

        reg.setLicenseUrl(licenseUrl);
        reg.setStep(2);
        registrationRepository.save(reg);

        Map<String, Object> result = new HashMap<>();
        result.put("registrationId", reg.getId());
        result.put("licenseUrl", licenseUrl);
        result.put("ocrResult", ocrResult);
        result.put("step", reg.getStep());
        return result;
    }

    /**
     * Step 2: Fill enterprise basic info + select industry
     */
    @Transactional
    public EnterpriseRegistration fillEnterpriseInfo(Long userId, Map<String, String> info) {
        EnterpriseRegistration reg = registrationRepository
                .findByUserIdAndStatus(userId, "DRAFT")
                .orElseThrow(() -> new BusinessException("请先上传营业执照"));

        if (info.containsKey("creditCode")) reg.setCreditCode(info.get("creditCode"));
        if (info.containsKey("enterpriseName")) reg.setEnterpriseName(info.get("enterpriseName"));
        if (info.containsKey("legalPerson")) reg.setLegalPerson(info.get("legalPerson"));
        if (info.containsKey("address")) reg.setAddress(info.get("address"));
        if (info.containsKey("phone")) reg.setPhone(info.get("phone"));
        if (info.containsKey("industry")) reg.setIndustry(info.get("industry"));
        if (info.containsKey("industryTypeCode")) reg.setIndustryTypeCode(info.get("industryTypeCode"));
        if (info.containsKey("area")) reg.setArea(info.get("area"));

        reg.setStep(3);
        return registrationRepository.save(reg);
    }

    /**
     * Step 3: Upload qualification certificates
     */
    @Transactional
    public EnterpriseRegistration uploadQualifications(Long userId, List<MultipartFile> files) {
        EnterpriseRegistration reg = registrationRepository
                .findByUserIdAndStatus(userId, "DRAFT")
                .orElseThrow(() -> new BusinessException("请先填写企业基本信息"));

        List<String> urls = new ArrayList<>(parseUrlList(reg.getQualificationUrls()));

        for (MultipartFile file : files) {
            String url = minioService.uploadFile(file, "qualification");
            urls.add(url);
        }

        // 存为标准JSON数组字符串（兼容旧"[a, b]"格式的读取）
        reg.setQualificationUrls(toJsonArray(urls));
        reg.setStep(4);
        return registrationRepository.save(reg);
    }

    /** 序列化为标准JSON数组 */
    private String toJsonArray(List<String> urls) {
        try {
            return objectMapper.writeValueAsString(urls);
        } catch (Exception e) {
            log.warn("资质URL JSON序列化失败，降级为toString: {}", e.getMessage());
            return urls.toString();
        }
    }

    /** 解析资质URL列表：优先标准JSON数组，兼容旧"[a, b]"格式 */
    private List<String> parseUrlList(String stored) {
        if (stored == null || stored.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(objectMapper.readValue(stored, new TypeReference<List<String>>() {}));
        } catch (Exception ignored) {
            // 旧格式兜底：[url1, url2]
        }
        String cleaned = stored.replace("[", "").replace("]", "").replace("\"", "").trim();
        if (cleaned.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(cleaned.split(",")));
    }

    /**
     * Step 4: Upload storefront photo and interior photo
     */
    @Transactional
    public EnterpriseRegistration uploadStorePhotos(Long userId, MultipartFile storefrontPhoto, MultipartFile interiorPhoto) {
        EnterpriseRegistration reg = registrationRepository
                .findByUserIdAndStatus(userId, "DRAFT")
                .orElseThrow(() -> new BusinessException("请先上传资质证照"));

        if (storefrontPhoto != null && !storefrontPhoto.isEmpty()) {
            String url = minioService.uploadFile(storefrontPhoto, "storefront");
            reg.setStorefrontPhoto(url);
        }
        if (interiorPhoto != null && !interiorPhoto.isEmpty()) {
            String url = minioService.uploadFile(interiorPhoto, "interior");
            reg.setInteriorPhoto(url);
        }

        reg.setStep(5);
        return registrationRepository.save(reg);
    }

    /**
     * Step 5: Submit registration for review
     */
    @Transactional
    public EnterpriseRegistration submitRegistration(Long userId) {
        EnterpriseRegistration reg = registrationRepository
                .findByUserIdAndStatus(userId, "DRAFT")
                .orElseThrow(() -> new BusinessException("未找到注册草稿"));

        // Validate required fields
        if (reg.getCreditCode() == null || reg.getCreditCode().isBlank()) {
            throw new BusinessException("统一社会信用代码不能为空");
        }
        if (reg.getEnterpriseName() == null || reg.getEnterpriseName().isBlank()) {
            throw new BusinessException("企业名称不能为空");
        }
        if (reg.getLicenseUrl() == null || reg.getLicenseUrl().isBlank()) {
            throw new BusinessException("请上传营业执照");
        }

        // Check duplicate credit code
        if (registrationRepository.existsByCreditCodeAndStatus(reg.getCreditCode(), "SUBMITTED")) {
            throw new BusinessException("该信用代码已有提交中的注册申请");
        }
        if (enterpriseRepository.findByCreditCode(reg.getCreditCode()).isPresent()) {
            throw new BusinessException("该企业已注册");
        }

        reg.setStatus("SUBMITTED");
        return registrationRepository.save(reg);
    }

    /**
     * Get current registration status
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRegistrationStatus(Long userId) {
        Optional<EnterpriseRegistration> regOpt = registrationRepository
                .findByUserIdAndStatus(userId, "DRAFT");

        if (regOpt.isEmpty()) {
            // Check for submitted ones
            List<EnterpriseRegistration> allRegs = registrationRepository
                    .findByUserIdOrderByCreateTimeDesc(userId);
            if (!allRegs.isEmpty()) {
                EnterpriseRegistration latest = allRegs.get(0);
                Map<String, Object> result = new HashMap<>();
                result.put("registrationId", latest.getId());
                result.put("step", latest.getStep());
                result.put("status", latest.getStatus());
                result.put("reviewComment", latest.getReviewComment());
                result.put("enterpriseName", latest.getEnterpriseName());
                return result;
            }
            return Map.of("step", 0, "status", "NONE");
        }

        EnterpriseRegistration reg = regOpt.get();
        Map<String, Object> result = new HashMap<>();
        result.put("registrationId", reg.getId());
        result.put("step", reg.getStep());
        result.put("status", reg.getStatus());
        result.put("creditCode", reg.getCreditCode());
        result.put("enterpriseName", reg.getEnterpriseName());
        result.put("legalPerson", reg.getLegalPerson());
        result.put("address", reg.getAddress());
        result.put("phone", reg.getPhone());
        result.put("industry", reg.getIndustry());
        result.put("industryTypeCode", reg.getIndustryTypeCode());
        result.put("area", reg.getArea());
        result.put("licenseUrl", reg.getLicenseUrl());
        result.put("qualificationUrls", reg.getQualificationUrls());
        return result;
    }

    /**
     * Admin: Approve registration - create user + enterprise
     */
    @Transactional
    public Enterprise approveRegistration(Long registrationId, Long reviewerId, String comment) {
        EnterpriseRegistration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("注册申请不存在"));

        if (!"SUBMITTED".equals(reg.getStatus())) {
            throw new BusinessException("该申请不在待审核状态");
        }

        // Create or find user
        SysUser user;
        Optional<SysUser> existingUser = sysUserRepository.findByPhone(reg.getPhone());
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // 按角色编码解析企业用户角色（无则回退id=10）
            Long enterpriseRoleId = sysRoleRepository.findByRoleCode(ENTERPRISE_ROLE_CODE)
                    .map(SysRole::getId).orElse(10L);
            // Create enterprise user with phone as username
            String username = "ent_" + reg.getCreditCode();
            user = SysUser.builder()
                    .username(username)
                    .password(passwordEncoder.encode(DEFAULT_ENTERPRISE_PASSWORD)) // default password
                    .realName(reg.getLegalPerson())
                    .phone(reg.getPhone())
                    .userType("enterprise_user")
                    .roleId(enterpriseRoleId)
                    .status(1)
                    .build();
            user = sysUserRepository.save(user);
        }

        // Create enterprise
        Enterprise enterprise = Enterprise.builder()
                .creditCode(reg.getCreditCode())
                .name(reg.getEnterpriseName())
                .legalPerson(reg.getLegalPerson())
                .address(reg.getAddress())
                .phone(reg.getPhone())
                .industry(reg.getIndustry())
                .industryTypeCode(reg.getIndustryTypeCode())
                .area(reg.getArea())
                .licenseUrl(reg.getLicenseUrl())
                .qualificationUrls(reg.getQualificationUrls())
                .storefrontPhoto(reg.getStorefrontPhoto())
                .interiorPhoto(reg.getInteriorPhoto())
                .userId(user.getId())
                .registrationStatus("APPROVED")
                .status(1)
                .build();
        enterprise = enterpriseRepository.save(enterprise);

        // Update registration
        reg.setStatus("APPROVED");
        reg.setReviewedBy(reviewerId);
        reg.setReviewComment(comment);
        reg.setReviewedAt(java.time.LocalDateTime.now());
        registrationRepository.save(reg);

        return enterprise;
    }

    /**
     * Admin: Reject registration
     */
    @Transactional
    public void rejectRegistration(Long registrationId, Long reviewerId, String comment) {
        EnterpriseRegistration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("注册申请不存在"));

        if (!"SUBMITTED".equals(reg.getStatus())) {
            throw new BusinessException("该申请不在待审核状态");
        }

        reg.setStatus("REJECTED");
        reg.setReviewedBy(reviewerId);
        reg.setReviewComment(comment);
        reg.setReviewedAt(java.time.LocalDateTime.now());
        registrationRepository.save(reg);

        // 发布审批退回事件，触发预警
        Long entId = null;
        if (reg.getCreditCode() != null) {
            entId = enterpriseRepository.findByCreditCode(reg.getCreditCode()).map(Enterprise::getId).orElse(null);
        }
        eventPublisher.publishEvent(new RegistrationRejectedEvent(this, entId,
                Map.of("reason", comment != null ? comment : "审核未通过", "enterpriseName", reg.getEnterpriseName())));
    }

    /**
     * Admin: List pending registrations
     */
    @Transactional(readOnly = true)
    public List<EnterpriseRegistration> listPendingRegistrations() {
        return registrationRepository.findByStatusOrderByCreateTimeDesc("SUBMITTED");
    }

    /**
     * Admin: List all registrations
     */
    @Transactional(readOnly = true)
    public List<EnterpriseRegistration> listAllRegistrations() {
        return registrationRepository.findByStatusInOrderByCreateTimeDesc(
                Arrays.asList("DRAFT", "SUBMITTED", "APPROVED", "REJECTED"));
    }
}
