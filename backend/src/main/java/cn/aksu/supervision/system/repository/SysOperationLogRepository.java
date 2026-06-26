package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SysOperationLogRepository extends JpaRepository<SysOperationLog, Long> {

    Page<SysOperationLog> findByUsernameOrderByCreateTimeDesc(String username, Pageable pageable);

    @Query("SELECT l FROM SysOperationLog l WHERE " +
           "(:username IS NULL OR l.username LIKE %:username%) AND " +
           "(:operation IS NULL OR l.operation LIKE %:operation%)")
    Page<SysOperationLog> findByConditions(@Param("username") String username,
                                            @Param("operation") String operation,
                                            Pageable pageable);
}
