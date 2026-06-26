package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUsername(String username);

    Optional<SysUser> findByWxOpenid(String wxOpenid);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Page<SysUser> findByUserType(String userType, Pageable pageable);

    Page<SysUser> findByUserTypeIn(java.util.List<String> userTypes, Pageable pageable);

    long countByUserType(String userType);
}
