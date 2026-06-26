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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseContactRepository enterpriseContactRepository;

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
                                                      Integer status, int page, int size) {
        Page<Enterprise> pageData = enterpriseRepository.findByConditions(keyword, industry, area, status,
                PageRequest.of(page - 1, size));

        List<EnterpriseDTO> list = pageData.getContent().stream().map(this::toDTO).toList();
        return PageResult.of(list, pageData.getTotalElements(), page, size);
    }

    public void updateEnterpriseStatus(Long id, Integer status) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("企业不存在"));
        enterprise.setStatus(status);
        enterpriseRepository.save(enterprise);
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
                .userId(entity.getUserId())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
