package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.EnterpriseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnterpriseTypeRepository extends JpaRepository<EnterpriseType, Long> {

    Optional<EnterpriseType> findByTypeCode(String typeCode);

    Page<EnterpriseType> findByCategory(String category, Pageable pageable);

    @Query("SELECT et FROM EnterpriseType et WHERE " +
           "(:keyword IS NULL OR et.typeName LIKE %:keyword% OR et.typeCode LIKE %:keyword%) AND " +
           "(:category IS NULL OR et.category = :category)")
    Page<EnterpriseType> search(@Param("keyword") String keyword,
                                @Param("category") String category,
                                Pageable pageable);

    List<EnterpriseType> findByStatusOrderBySortOrder(Integer status);

    List<EnterpriseType> findAllByOrderBySortOrder();

    boolean existsByTypeCode(String typeCode);
}
