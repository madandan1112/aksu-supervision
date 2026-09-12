package cn.aksu.supervision.alert.listener;

import cn.aksu.supervision.alert.engine.AlertRuleEngine;
import cn.aksu.supervision.alert.entity.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 预警事件监听器
 * 监听业务事件并实时触发预警规则
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventListener {

    private final AlertRuleEngine alertRuleEngine;

    /**
     * 监听检查不合格事件
     */
    @Async
    @EventListener
    public void onInspectionFailed(InspectionFailedEvent event) {
        log.info("收到检查不合格事件: enterpriseId={}", event.getEnterpriseId());
        try {
            Alert alert = alertRuleEngine.fireEventRule("INSPECTION_FAILED", event.getEnterpriseId(), event.getContext());
            if (alert != null) {
                log.info("事件触发预警: id={}, type={}", alert.getId(), alert.getAlertType());
            }
        } catch (Exception e) {
            log.error("处理检查不合格事件失败", e);
        }
    }

    /**
     * 监听审批退回事件
     */
    @Async
    @EventListener
    public void onRegistrationRejected(RegistrationRejectedEvent event) {
        log.info("收到审批退回事件: enterpriseId={}", event.getEnterpriseId());
        try {
            Alert alert = alertRuleEngine.fireEventRule("REGISTRATION_REJECTED", event.getEnterpriseId(), event.getContext());
            if (alert != null) {
                log.info("事件触发预警: id={}, type={}", alert.getId(), alert.getAlertType());
            }
        } catch (Exception e) {
            log.error("处理审批退回事件失败", e);
        }
    }

    /**
     * 监听整改反馈事件
     */
    @Async
    @EventListener
    public void onRectificationSubmitted(RectificationSubmittedEvent event) {
        log.info("收到整改反馈事件: enterpriseId={}", event.getEnterpriseId());
        try {
            Alert alert = alertRuleEngine.fireEventRule("RECTIFICATION_SUBMITTED", event.getEnterpriseId(), event.getContext());
            if (alert != null) {
                log.info("事件触发预警: id={}, type={}", alert.getId(), alert.getAlertType());
            }
        } catch (Exception e) {
            log.error("处理整改反馈事件失败", e);
        }
    }
}
