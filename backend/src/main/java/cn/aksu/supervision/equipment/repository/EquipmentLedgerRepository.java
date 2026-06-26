package cn.aksu.supervision.equipment.repository;

import cn.aksu.supervision.equipment.entity.EquipmentLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentLedgerRepository extends JpaRepository<EquipmentLedger, Long> {

    Page<EquipmentLedger> findByEnterpriseIdOrderByCreateTimeDesc(Long enterpriseId, Pageable pageable);
}
