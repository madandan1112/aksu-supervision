package cn.aksu.supervision.report.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.report.dto.ReportUploadRequest;
import cn.aksu.supervision.enterprise.entity.Enterprise;
import cn.aksu.supervision.enterprise.repository.EnterpriseRepository;
import cn.aksu.supervision.report.entity.ComplianceReport;
import cn.aksu.supervision.report.repository.ComplianceReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceReportService {

    private final ComplianceReportRepository reportRepository;
    private final EnterpriseRepository enterpriseRepository;

    public ComplianceReport uploadReport(Long enterpriseId, ReportUploadRequest request, String uploadedBy) {
        ComplianceReport report = ComplianceReport.builder()
                .enterpriseId(enterpriseId)
                .title(request.getTitle())
                .reportType(request.getReportType())
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .expireDate(request.getExpireDate())
                .status("PENDING")
                .uploadedBy(uploadedBy)
                .build();

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public PageResult<ComplianceReport> listByEnterprise(Long enterpriseId, int page, int size) {
        Page<ComplianceReport> pageData = reportRepository.findByEnterpriseIdOrderByCreateTimeDesc(
                enterpriseId, PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public PageResult<ComplianceReport> listForAdmin(String status, String keyword, int page, int size) {
        Page<ComplianceReport> pageData = reportRepository.findByConditions(status, keyword,
                PageRequest.of(page - 1, size));
        List<ComplianceReport> reports = pageData.getContent();
        // 批量查询企业名称并填充
        if (!reports.isEmpty()) {
            List<Long> enterpriseIds = reports.stream()
                    .map(ComplianceReport::getEnterpriseId)
                    .distinct()
                    .toList();
            List<Enterprise> enterprises = enterpriseRepository.findAllById(enterpriseIds);
            java.util.Map<Long, String> enterpriseNameMap = enterprises.stream()
                    .collect(java.util.stream.Collectors.toMap(Enterprise::getId, Enterprise::getName));
            for (ComplianceReport report : reports) {
                report.setEnterpriseName(enterpriseNameMap.getOrDefault(report.getEnterpriseId(), "-"));
                report.setReportNo(report.getTitle());
            }
        }
        return PageResult.of(reports, pageData.getTotalElements(), page, size);
    }

    public ComplianceReport reviewReport(Long id, String status, String comment, String reviewedBy) {
        ComplianceReport report = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("报告不存在"));

        report.setStatus(status);
        report.setReviewComment(comment);
        report.setReviewedBy(reviewedBy);
        report.setReviewTime(LocalDateTime.now());

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<ComplianceReport> getExpiringReports() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(30);
        return reportRepository.findExpiringReports(now, deadline);
    }

    public ComplianceReport updateReport(Long id, ReportUploadRequest request) {
        ComplianceReport report = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("报告不存在"));
        
        if (request.getTitle() != null) report.setTitle(request.getTitle());
        if (request.getReportType() != null) report.setReportType(request.getReportType());
        if (request.getFileUrl() != null) report.setFileUrl(request.getFileUrl());
        if (request.getFileName() != null) report.setFileName(request.getFileName());
        if (request.getExpireDate() != null) report.setExpireDate(request.getExpireDate());
        
        // Reset status to PENDING when re-uploaded
        report.setStatus("PENDING");
        report.setReviewComment(null);
        report.setReviewedBy(null);
        report.setReviewTime(null);
        
        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public ComplianceReport getReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("报告不存在"));
    }
}
