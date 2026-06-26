package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysPositionRepository extends JpaRepository<SysPosition, Long> {

    Optional<SysPosition> findByPositionCode(String positionCode);

    List<SysPosition> findByOrgIdOrderBySortOrder(Long orgId);

    List<SysPosition> findByStatusOrderBySortOrder(Integer status);

    List<SysPosition> findByCategoryAndStatusOrderBySortOrder(String category, Integer status);

    boolean existsByPositionCode(String positionCode);
}