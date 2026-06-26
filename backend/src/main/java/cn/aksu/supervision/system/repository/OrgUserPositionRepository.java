package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.OrgUserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgUserPositionRepository extends JpaRepository<OrgUserPosition, Long> {

    List<OrgUserPosition> findByUserIdAndStatus(Long userId, Integer status);

    List<OrgUserPosition> findByOrgIdAndStatus(Long orgId, Integer status);

    List<OrgUserPosition> findByPositionIdAndStatus(Long positionId, Integer status);

    List<OrgUserPosition> findByJobTitleIdAndStatus(Long jobTitleId, Integer status);

    Optional<OrgUserPosition> findByUserIdAndOrgId(Long userId, Long orgId);

    List<OrgUserPosition> findByUserId(Long userId);

    @Query("SELECT oup FROM OrgUserPosition oup WHERE oup.userId = :userId AND oup.isPrimary = 1 AND oup.status = 1")
    Optional<OrgUserPosition> findPrimaryByUserId(@Param("userId") Long userId);

    long countByOrgIdAndStatus(Long orgId, Integer status);
}