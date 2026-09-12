package cn.aksu.supervision.alert.listener;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class InspectionFailedEvent extends ApplicationEvent {

    private final Long enterpriseId;
    private final Map<String, Object> context;

    public InspectionFailedEvent(Object source, Long enterpriseId, Map<String, Object> context) {
        super(source);
        this.enterpriseId = enterpriseId;
        this.context = context;
    }

    public Long getEnterpriseId() { return enterpriseId; }
    public Map<String, Object> getContext() { return context; }
}
