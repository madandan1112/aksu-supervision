package cn.aksu.supervision.system.repository;

import cn.aksu.supervision.system.entity.SysConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysConfigRepository extends JpaRepository<SysConfig, Long> {

    Optional<SysConfig> findByConfigKey(String configKey);
}
