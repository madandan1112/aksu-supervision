package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.UserDataScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataScopeRepository extends JpaRepository<UserDataScope, Long> {

    List<UserDataScope> findByUserId(Long userId);

    List<UserDataScope> findByUserIdAndOrgType(Long userId, String orgType);

    void deleteByUserId(Long userId);
}
