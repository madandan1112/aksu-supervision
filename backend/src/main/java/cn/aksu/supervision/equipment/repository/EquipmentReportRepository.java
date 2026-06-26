package cn.aksu.supervision.equipment.repository;

import cn.aksu.supervision.equipment.entity.EquipmentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentReportRepository extends JpaRepository<EquipmentReport, Long> {

    List<EquipmentReport> findByEquipmentIdOrderByCreateTimeDesc(Long equipmentId);
}
