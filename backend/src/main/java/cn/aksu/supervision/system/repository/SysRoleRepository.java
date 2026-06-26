package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByRoleCode(String roleCode);
}
