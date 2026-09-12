package cn.aksu.supervision.alert.scheduler;

import cn.aksu.supervision.alert.engine.AlertRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 预警定时扫描调度器
 * - 每日凌晨2:00执行全量规则扫描
 * - 每小时检查督办升级
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertScheduler {

    private final AlertRuleEngine alertRuleEngine;
    private final AlertEscalationService escalationService;

    /**
     * 每日凌晨2:00执行全量预警规则扫描
     * Cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyScan() {
        log.info("===== 预警定时扫描开始 =====");
        long start = System.currentTimeMillis();
        try {
            int count = alertRuleEngine.executeScheduledRules();
            long elapsed = System.currentTimeMillis() - start;
            log.info("===== 预警定时扫描完成: 新增{}条预警, 耗时{}ms =====", count, elapsed);
        } catch (Exception e) {
            log.error("预警定时扫描异常", e);
        }
    }

    /**
     * 每小时检查督办升级
     * 超过24h未响应的预警自动升级
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyEscalation() {
        log.info("===== 预警督办升级检查开始 =====");
        try {
            int count = escalationService.checkAndEscalate();
            log.info("===== 预警督办升级检查完成: 升级{}条 =====", count);
        } catch (Exception e) {
            log.error("预警督办升级检查异常", e);
        }
    }
}
