package cn.aksu.supervision.enterprise.repository;

import cn.aksu.supervision.enterprise.entity.EnterpriseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnterpriseRegistrationRepository extends JpaRepository<EnterpriseRegistration, Long> {

    Optional<EnterpriseRegistration> findByUserIdAndStatus(Long userId, String status);

    List<EnterpriseRegistration> findByUserIdOrderByCreateTimeDesc(Long userId);

    Optional<EnterpriseRegistration> findByUserIdAndStep(Long userId, Integer step);

    List<EnterpriseRegistration> findByStatusOrderByCreateTimeDesc(String status);

    List<EnterpriseRegistration> findByStatusInOrderByCreateTimeDesc(List<String> statuses);

    boolean existsByCreditCodeAndStatus(String creditCode, String status);
}
