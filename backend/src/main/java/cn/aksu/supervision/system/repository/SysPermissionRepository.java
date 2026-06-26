package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {

    Optional<SysPermission> findByPermissionCode(String permissionCode);
}
