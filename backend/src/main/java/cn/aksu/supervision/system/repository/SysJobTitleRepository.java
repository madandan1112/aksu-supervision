package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysJobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysJobTitleRepository extends JpaRepository<SysJobTitle, Long> {

    Optional<SysJobTitle> findByTitleCode(String titleCode);

    List<SysJobTitle> findByStatusOrderBySortOrder(Integer status);

    List<SysJobTitle> findByCategoryAndStatusOrderBySortOrder(String category, Integer status);

    List<SysJobTitle> findByIsLeadershipAndStatusOrderByLevel(Integer isLeadership, Integer status);

    boolean existsByTitleCode(String titleCode);
}