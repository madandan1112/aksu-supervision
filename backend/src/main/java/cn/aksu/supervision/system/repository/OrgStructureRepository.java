package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.OrgStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrgStructureRepository extends JpaRepository<OrgStructure, Long> {

    List<OrgStructure> findByParentIdIsNullOrderBySortOrder();

    List<OrgStructure> findByParentIdOrderBySortOrder(Long parentId);

    List<OrgStructure> findByLevelOrderBySortOrder(Integer level);

    boolean existsByParentId(Long parentId);

    long countByParentId(Long parentId);
}
