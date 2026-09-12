package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.RolePermissionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionDetailRepository extends JpaRepository<RolePermissionDetail, Long> {

    List<RolePermissionDetail> findByRoleId(Long roleId);

    List<RolePermissionDetail> findByRoleIdIn(List<Long> roleIds);

    List<RolePermissionDetail> findByRoleIdAndStatus(Long roleId, Integer status);

    List<RolePermissionDetail> findByModuleAndRoleId(String module, Long roleId);

    void deleteByRoleId(Long roleId);

    boolean existsByRoleIdAndPermissionCode(Long roleId, String permissionCode);
}
