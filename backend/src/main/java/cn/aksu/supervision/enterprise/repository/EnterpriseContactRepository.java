package cn.aksu.supervision.enterprise.repository;

import cn.aksu.supervision.enterprise.entity.EnterpriseContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnterpriseContactRepository extends JpaRepository<EnterpriseContact, Long> {

    List<EnterpriseContact> findByEnterpriseIdOrderByIsPrimaryDescCreateTimeDesc(Long enterpriseId);

    List<EnterpriseContact> findByEnterpriseIdAndIsPrimary(Long enterpriseId, Integer isPrimary);
}
