package cn.aksu.supervision.enterprise.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.enterprise.dto.ContactRequest;
import cn.aksu.supervision.enterprise.dto.EnterpriseDTO;
import cn.aksu.supervision.enterprise.dto.EnterpriseUpdateRequest;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.entity.EnterpriseContact;
import cn.aksu.supervision.enterprise.repository.EnterpriseContactRepository;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.inspection.entity.InspectionRecord;
import cn.aksu.supervision.inspection.repository.InspectionRecordRepository;
import cn.aksu.supervision.rectification.entity.RectificationNotice;
import cn.aksu.supervision.rectification.repository.RectificationNoticeRepository;
import cn.aksu.supervision.report.entity.ComplianceReport;
import cn.aksu.supervision.report.repository.ComplianceReportRepository;
import cn.aksu.supervision.enterprise.dto.RegulatoryInfoDTO;
import cn.aksu.supervision.system.dto.DataPermissionContext;
import cn.aksu.supervision.system.entity.OrgStructure;
import cn.aksu.supervision.system.entity.SysUser;
import cn.aksu.supervision.system.repository.OrgStructureRepository;
import cn.aksu.supervision.system.repository.SysUserRepository;
import cn.aksu.supervision.system.service.DataPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseContactRepository enterpriseContactRepository;
    private final DataPermissionService dataPermissionService;
    private final RectificationNoticeRepository rectificationNoticeRepository;
    private final InspectionRecordRepository inspectionRecordRepository;
    private final ComplianceReportRepository complianceReportRepository;
    private final OrgStructureRepository orgStructureRepository;
    private final SysUserRepository sysUserRepository;

    public EnterpriseDTO getEnterpriseByUserId(Long userId) {
        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("企业信息不存在"));
        return toDTO(enterprise);
    }

    public EnterpriseDTO scanQuery(String code) {
        // 先尝试按ID查询
        try {
            Long id = Long.parseLong(code);
            Enterprise enterprise = enterpriseRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("企业不存在"));
            return toDTO(enterprise);
        } catch (NumberFormatException e) {
            // 不是数字，按信用代码查询
            Enterprise enterprise = enterpriseRepository.findByCreditCode(code)
                    .orElseThrow(() -> new BusinessException("企业不存在"));
            return toDTO(enterprise);
        }
    }

    public EnterpriseDTO getEnterpriseById(Long id) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("企业不存在"));
        return toDTO(enterprise);
    }

    public EnterpriseDTO updateEnterprise(Long userId, EnterpriseUpdateRequest request) {
        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("企业信息不存在"));

        if (request.getName() != null) enterprise.setName(request.getName());
        if (request.getLegalPerson() != null) enterprise.setLegalPerson(request.getLegalPerson());
        if (request.getPhone() != null) enterprise.setPhone(request.getPhone());
        if (request.getEmail() != null) enterprise.setEmail(request.getEmail());
        if (request.getIndustry() != null) enterprise.setIndustry(request.getIndustry());
        if (request.getArea() != null) enterprise.setArea(request.getArea());
        if (request.getAddress() != null) enterprise.setAddress(request.getAddress());
        if (request.getBusinessScope() != null) enterprise.setBusinessScope(request.getBusinessScope());
        if (request.getLicenseUrl() != null) enterprise.setLicenseUrl(request.getLicenseUrl());
        if (request.getStorefrontPhoto() != null) enterprise.setStorefrontPhoto(request.getStorefrontPhoto());
        if (request.getInteriorPhoto() != null) enterprise.setInteriorPhoto(request.getInteriorPhoto());
        if (request.getQualificationUrls() != null) enterprise.setQualificationUrls(request.getQualificationUrls());

        enterpriseRepository.save(enterprise);
        return toDTO(enterprise);
    }

    public EnterpriseContact addContact(Long userId, ContactRequest request) {
        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("企业信息不存在"));

        EnterpriseContact contact = EnterpriseContact.builder()
                .enterpriseId(enterprise.getId())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .position(request.getPosition())
                .email(request.getEmail())
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : 0)
                .build();

        return enterpriseContactRepository.save(contact);
    }

    public EnterpriseContact updateContact(Long id, Long userId, ContactRequest request) {
        EnterpriseContact contact = enterpriseContactRepository.findById(id)
                .orElseThrow(() -> new BusinessException("联系人不存在"));

        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("企业信息不存在"));

        if (!contact.getEnterpriseId().equals(enterprise.getId())) {
            throw new BusinessException("无权操作此联系人");
        }

        contact.setContactName(request.getContactName());
        contact.setContactPhone(request.getContactPhone());
        contact.setPosition(request.getPosition());
        contact.setEmail(request.getEmail());
        if (request.getIsPrimary() != null) contact.setIsPrimary(request.getIsPrimary());

        return enterpriseContactRepository.save(contact);
    }

    @Transactional(readOnly = true)
    public List<EnterpriseContact> getContacts(Long userId) {
        Enterprise enterprise = enterpriseRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("企业信息不存在"));
        return enterpriseContactRepository.findByEnterpriseIdOrderByIsPrimaryDescCreateTimeDesc(enterprise.getId());
    }

    @Transactional(readOnly = true)
    public PageResult<EnterpriseDTO> listEnterprises(String keyword, String industry, String area,
                                                       Integer status, int page, int size, Long currentUserId) {
        // 获取当前用户数据权限
        DataPermissionContext ctx = dataPermissionService.getDataPermission(currentUserId);

        // 确定可查看的区域列表
        List<String> accessibleAreas = dataPermissionService.getAccessibleAreas(currentUserId);

        // 如果用户只有SELF权限（企业用户），只能看自己的企业
        if (ctx.isSelfOnly()) {
            Enterprise own = enterpriseRepository.findByUserId(currentUserId).orElse(null);
            if (own == null) {
                return PageResult.of(List.of(), 0, page, size);
            }
            return PageResult.of(List.of(toDTO(own)), 1, page, size);
        }

        // 管理端：非管理员由下方 findByConditionsAndAreas 按归一化区域自动过滤

        Page<Enterprise> pageData;
        if (ctx.isAdmin()) {
            // 管理员：不限制区域
            pageData = enterpriseRepository.findByConditions(keyword, industry, area, status,
                    PageRequest.of(page - 1, size));
        } else {
            // 非管理员：按可访问区域过滤（归一化县市短名；空集用哨兵值兜底返回空页）
            pageData = enterpriseRepository.findByConditionsAndAreas(keyword, industry, area, status,
                    accessibleAreas.isEmpty() ? List.of("__NO_ACCESS__") : accessibleAreas,
                    PageRequest.of(page - 1, size));
        }

        List<EnterpriseDTO> list = pageData.getContent().stream().map(this::toDTO).toList();
        return PageResult.of(list, pageData.getTotalElements(), page, size);
    }

    public void updateEnterpriseStatus(Long id, Integer status) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("企业不存在"));
        enterprise.setStatus(status);
        enterpriseRepository.save(enterprise);
    }

    public EnterpriseDTO adminUpdateEnterprise(Long id, EnterpriseUpdateRequest request) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("企业不存在"));

        if (request.getName() != null) enterprise.setName(request.getName());
        if (request.getCreditCode() != null) enterprise.setCreditCode(request.getCreditCode());
        if (request.getLegalPerson() != null) enterprise.setLegalPerson(request.getLegalPerson());
        if (request.getPhone() != null) enterprise.setPhone(request.getPhone());
        if (request.getEmail() != null) enterprise.setEmail(request.getEmail());
        if (request.getIndustry() != null) enterprise.setIndustry(request.getIndustry());
        if (request.getArea() != null) enterprise.setArea(request.getArea());
        if (request.getAddress() != null) enterprise.setAddress(request.getAddress());
        if (request.getBusinessScope() != null) enterprise.setBusinessScope(request.getBusinessScope());
        if (request.getLicenseUrl() != null) enterprise.setLicenseUrl(request.getLicenseUrl());
        if (request.getStorefrontPhoto() != null) enterprise.setStorefrontPhoto(request.getStorefrontPhoto());
        if (request.getInteriorPhoto() != null) enterprise.setInteriorPhoto(request.getInteriorPhoto());
        if (request.getQualificationUrls() != null) enterprise.setQualificationUrls(request.getQualificationUrls());

        enterpriseRepository.save(enterprise);
        return toDTO(enterprise);
    }

    private EnterpriseDTO toDTO(Enterprise entity) {
        return EnterpriseDTO.builder()
                .id(entity.getId())
                .creditCode(entity.getCreditCode())
                .name(entity.getName())
                .legalPerson(entity.getLegalPerson())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .industry(entity.getIndustry())
                .area(entity.getArea())
                .address(entity.getAddress())
                .businessScope(entity.getBusinessScope())
                .licenseUrl(entity.getLicenseUrl())
                .status(entity.getStatus())
                .statusText(calculateStatus(entity))
                .statusDetail(calculateStatusDetail(entity))
                .userId(entity.getUserId())
                .registrationStatus(entity.getRegistrationStatus())
                .qualificationUrls(entity.getQualificationUrls())
                .industryTypeCode(entity.getIndustryTypeCode())
                .storefrontPhoto(entity.getStorefrontPhoto())
                .interiorPhoto(entity.getInteriorPhoto())
                .reviewComment(entity.getReviewComment())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    private String calculateStatus(Enterprise enterprise) {
        if (enterprise.getStatus() == 0 || "CLOSED".equals(enterprise.getRegistrationStatus())) {
            return "已注销";
        }
        List<RectificationNotice> notices = rectificationNoticeRepository.findByEnterpriseIdAndStatusNotIn(enterprise.getId(), java.util.List.of("ACCEPTED", "COMPLETED"));
        if (!notices.isEmpty()) return "异常";
        List<InspectionRecord> inspections = inspectionRecordRepository.findByEnterpriseIdAndStatus(enterprise.getId(), "DRAFT");
        if (!inspections.isEmpty()) return "异常";
        List<ComplianceReport> reports = complianceReportRepository.findExpiringReportsByEnterpriseId(enterprise.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        if (!reports.isEmpty()) return "异常";
        List<ComplianceReport> expiredReports = complianceReportRepository.findExpiredReportsByEnterpriseId(enterprise.getId(), LocalDateTime.now());
        if (!expiredReports.isEmpty()) return "异常";
        return "正常";
    }

    private String calculateStatusDetail(Enterprise enterprise) {
        StringBuilder sb = new StringBuilder();
        if (enterprise.getStatus() == 0 || "CLOSED".equals(enterprise.getRegistrationStatus())) {
            return "⛔ 已注销";
        }
        List<RectificationNotice> notices = rectificationNoticeRepository.findByEnterpriseIdAndStatusNotIn(enterprise.getId(), java.util.List.of("ACCEPTED", "COMPLETED"));
        if (!notices.isEmpty()) sb.append(notices.size()).append("项整改未反馈\n");
        List<InspectionRecord> inspections = inspectionRecordRepository.findByEnterpriseIdAndStatus(enterprise.getId(), "DRAFT");
        if (!inspections.isEmpty()) sb.append(inspections.size()).append("项检查未完成\n");
        List<ComplianceReport> reports = complianceReportRepository.findExpiringReportsByEnterpriseId(enterprise.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        if (!reports.isEmpty()) sb.append(reports.size()).append("份报告即将到期\n");
        List<ComplianceReport> expiredReports = complianceReportRepository.findExpiredReportsByEnterpriseId(enterprise.getId(), LocalDateTime.now());
        if (!expiredReports.isEmpty()) sb.append(expiredReports.size()).append("份报告已过期\n");
        return sb.isEmpty() ? "✅ 正常" : sb.toString().trim();
    }

    /**
     * 获取企业属地监管信息
     */
    @Transactional(readOnly = true)
    public RegulatoryInfoDTO getRegulatoryInfo(String creditCode) {
        // 查询企业
        Enterprise enterprise = enterpriseRepository.findByCreditCode(creditCode)
                .orElseThrow(() -> new BusinessException("企业不存在"));

        // 根据企业区域查找属地监管单位（从org_structure表查询）
        // 先查找level=2（县市）且名称匹配企业区域的组织
        List<OrgStructure> areaOrgs = orgStructureRepository.findByLevelOrderBySortOrder(2);
        OrgStructure regulatoryOrg = null;
        for (OrgStructure org : areaOrgs) {
            if (org.getName() != null && enterprise.getArea() != null &&
                (org.getName().contains(enterprise.getArea()) || enterprise.getArea().contains(org.getName()))) {
                regulatoryOrg = org;
                break;
            }
        }

        // 如果未找到县市级别，尝试查找level=3（乡镇街道）
        if (regulatoryOrg == null) {
            List<OrgStructure> townOrgs = orgStructureRepository.findByLevelOrderBySortOrder(3);
            for (OrgStructure org : townOrgs) {
                if (org.getName() != null && enterprise.getArea() != null &&
                    (org.getName().contains(enterprise.getArea()) || enterprise.getArea().contains(org.getName()))) {
                    regulatoryOrg = org;
                    break;
                }
            }
        }

        // 构建监管单位信息
        String regulatoryUnitName = regulatoryOrg != null ? regulatoryOrg.getName() : "阿克苏地区市场监督管理局";
        String regulatoryUnitAddress = regulatoryOrg != null && regulatoryOrg.getAddress() != null
                ? regulatoryOrg.getAddress() : "新疆阿克苏地区";
        String regulatoryUnitPhone = regulatoryOrg != null && regulatoryOrg.getLeaderPhone() != null
                ? regulatoryOrg.getLeaderPhone() : "-";

        // 查询该区域的执法人员列表（从sys_user表查询user_type=admin的执法人员）
        List<SysUser> allUsers = sysUserRepository.findByUserTypeIn(List.of("admin", "inspector"), null).getContent();
        List<RegulatoryInfoDTO.EnforcerDTO> enforcers = new ArrayList<>();
        for (SysUser user : allUsers) {
            // 筛选：用户有所属组织且与企业区域匹配，或有执法证号
            boolean isMatch = false;
            if (user.getOrgName() != null && enterprise.getArea() != null) {
                isMatch = user.getOrgName().contains(enterprise.getArea()) || enterprise.getArea().contains(user.getOrgName());
            }
            // 如果区域不匹配但用户有执法证号，也认为是执法人员
            if (!isMatch && user.getLawEnforcementNo() != null && !user.getLawEnforcementNo().isEmpty()) {
                isMatch = true;
            }
            if (isMatch && user.getStatus() != null && user.getStatus() == 1) {
                enforcers.add(RegulatoryInfoDTO.EnforcerDTO.builder()
                        .name(user.getRealName() != null ? user.getRealName() : user.getUsername())
                        .phone(user.getPhone() != null ? user.getPhone() : "-")
                        .photoUrl(user.getPhotoUrl())
                        .department(user.getOrgName() != null ? user.getOrgName() : "-")
                        .lawEnforcementNo(user.getLawEnforcementNo() != null ? user.getLawEnforcementNo() : "-")
                        .build());
            }
        }

        // 如果没有找到匹配的执法人员，返回默认信息
        if (enforcers.isEmpty()) {
            enforcers.add(RegulatoryInfoDTO.EnforcerDTO.builder()
                    .name("执法大队")
                    .phone(regulatoryUnitPhone)
                    .photoUrl(null)
                    .department(regulatoryUnitName)
                    .lawEnforcementNo("-")
                    .build());
        }

        return RegulatoryInfoDTO.builder()
                .enterprise(toDTO(enterprise))
                .regulatoryUnitName(regulatoryUnitName)
                .regulatoryUnitAddress(regulatoryUnitAddress)
                .regulatoryUnitPhone(regulatoryUnitPhone)
                .enforcers(enforcers)
                .build();
    }
}
