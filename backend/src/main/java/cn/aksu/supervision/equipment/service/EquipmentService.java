package cn.aksu.supervision.equipment.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.equipment.dto.EquipmentAddRequest;
import cn.aksu.supervision.equipment.dto.EquipmentReportRequest;
import cn.aksu.supervision.equipment.entity.EquipmentLedger;
import cn.aksu.supervision.equipment.entity.EquipmentReport;
import cn.aksu.supervision.equipment.repository.EquipmentLedgerRepository;
import cn.aksu.supervision.equipment.repository.EquipmentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentService {

    private final EquipmentLedgerRepository ledgerRepository;
    private final EquipmentReportRepository reportRepository;

    public EquipmentLedger addEquipment(Long enterpriseId, EquipmentAddRequest request) {
        EquipmentLedger equipment = EquipmentLedger.builder()
                .enterpriseId(enterpriseId)
                .name(request.getName())
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .location(request.getLocation())
                .installDate(request.getInstallDate())
                .nextInspectionDate(request.getNextInspectionDate())
                .certificateNo(request.getCertificateNo())
                .description(request.getDescription())
                .status("NORMAL")
                .build();

        return ledgerRepository.save(equipment);
    }

    @Transactional(readOnly = true)
    public PageResult<EquipmentLedger> listByEnterprise(Long enterpriseId, int page, int size) {
        Page<EquipmentLedger> pageData = ledgerRepository.findByEnterpriseIdOrderByCreateTimeDesc(
                enterpriseId, PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public EquipmentLedger updateEquipment(Long id, EquipmentAddRequest request) {
        EquipmentLedger equipment = ledgerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        equipment.setName(request.getName());
        equipment.setModel(request.getModel());
        equipment.setManufacturer(request.getManufacturer());
        equipment.setLocation(request.getLocation());
        equipment.setInstallDate(request.getInstallDate());
        equipment.setNextInspectionDate(request.getNextInspectionDate());
        equipment.setCertificateNo(request.getCertificateNo());
        equipment.setDescription(request.getDescription());

        return ledgerRepository.save(equipment);
    }

    public EquipmentReport reportStatus(Long equipmentId, EquipmentReportRequest request, String reporter) {
        EquipmentLedger equipment = ledgerRepository.findById(equipmentId)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        equipment.setStatus(request.getStatus());
        ledgerRepository.save(equipment);

        EquipmentReport report = EquipmentReport.builder()
                .equipmentId(equipmentId)
                .reportType(request.getReportType())
                .status(request.getStatus())
                .content(request.getContent())
                .images(request.getImages())
                .reporter(reporter)
                .build();

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<EquipmentReport> getReports(Long equipmentId) {
        return reportRepository.findByEquipmentIdOrderByCreateTimeDesc(equipmentId);
    }
}
