package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUsername(String username);

    Optional<SysUser> findByWxOpenid(String wxOpenid);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Optional<SysUser> findByPhone(String phone);

    Page<SysUser> findByUserType(String userType, Pageable pageable);

    Page<SysUser> findByUserTypeIn(List<String> userTypes, Pageable pageable);

    long countByUserType(String userType);

    Page<SysUser> findByUserTypeInAndStatus(List<String> userTypes, Integer status, Pageable pageable);

    @Query("SELECT u FROM SysUser u WHERE u.userType IN :types AND (u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.phone LIKE %:keyword%)")
    Page<SysUser> findByUserTypeInAndKeyword(@Param("types") List<String> types, @Param("keyword") String keyword, Pageable pageable);

    Page<SysUser> findByIdIn(List<Long> ids, Pageable pageable);

    Page<SysUser> findByIdInAndStatus(List<Long> ids, Integer status, Pageable pageable);

    @Query("SELECT u FROM SysUser u WHERE u.id IN :ids AND (u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.phone LIKE %:keyword%)")
    Page<SysUser> findByIdInAndKeyword(@Param("ids") List<Long> ids, @Param("keyword") String keyword, Pageable pageable);

    // ========== roleId based queries ==========

    Page<SysUser> findByUserTypeInAndRoleId(List<String> userTypes, Long roleId, Pageable pageable);

    Page<SysUser> findByUserTypeInAndRoleIdAndStatus(List<String> userTypes, Long roleId, Integer status, Pageable pageable);

    @Query("SELECT u FROM SysUser u WHERE u.userType IN :types AND u.roleId = :roleId AND (u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.phone LIKE %:keyword%)")
    Page<SysUser> findByUserTypeInAndRoleIdAndKeyword(@Param("types") List<String> types, @Param("roleId") Long roleId, @Param("keyword") String keyword, Pageable pageable);

    Page<SysUser> findByIdInAndRoleId(List<Long> ids, Long roleId, Pageable pageable);

    Page<SysUser> findByIdInAndRoleIdAndStatus(List<Long> ids, Long roleId, Integer status, Pageable pageable);

    @Query("SELECT u FROM SysUser u WHERE u.id IN :ids AND u.roleId = :roleId AND (u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.phone LIKE %:keyword%)")
    Page<SysUser> findByIdInAndRoleIdAndKeyword(@Param("ids") List<Long> ids, @Param("roleId") Long roleId, @Param("keyword") String keyword, Pageable pageable);
}
