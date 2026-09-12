package cn.aksu.supervision.alert.constants;

/**
 * 预警规则常量定义
 * 三大类12条规则：资质到期类、监管流程类、信用风险类
 */
public final class AlertConstants {

    private AlertConstants() {}

    // ========== 预警类型 ==========
    /** 许可证即将到期（60/30/7天） */
    public static final String TYPE_LICENSE_EXPIRING = "LICENSE_EXPIRING";
    /** 许可证已过期 */
    public static final String TYPE_LICENSE_EXPIRED = "LICENSE_EXPIRED";
    /** 整改超期未反馈 */
    public static final String TYPE_RECTIFICATION_OVERDUE = "RECTIFICATION_OVERDUE";
    /** 检查任务超期 */
    public static final String TYPE_INSPECTION_OVERDUE = "INSPECTION_OVERDUE";
    /** 合规报告未提交 */
    public static final String TYPE_REPORT_MISSING = "REPORT_MISSING";
    /** 注册备案异常 */
    public static final String TYPE_REGISTRATION_ANOMALY = "REGISTRATION_ANOMALY";
    /** 信用异常（被列入异常/注销） */
    public static final String TYPE_CREDIT_ANOMALY = "CREDIT_ANOMALY";
    /** 屡次违规 */
    public static final String TYPE_REPEAT_VIOLATION = "REPEAT_VIOLATION";

    // ========== 预警等级 ==========
    public static final String LEVEL_HIGH = "HIGH";
    public static final String LEVEL_MEDIUM = "MEDIUM";
    public static final String LEVEL_LOW = "LOW";

    // ========== 预警状态 ==========
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_HANDLING = "HANDLING";
    public static final String STATUS_HANDLED = "HANDLED";
    public static final String STATUS_ESCALATED = "ESCALATED";

    // ========== 预警来源 ==========
    public static final String SOURCE_SCHEDULED = "SCHEDULED";
    public static final String SOURCE_EVENT = "EVENT";

    // ========== 督办时限（小时） ==========
    public static final int ESCALATION_HOURS = 24;

    // ========== 去重时间窗口（小时） ==========
    public static final int DEDUP_HOURS = 24;

    // ========== 预警类型中文名映射 ==========
    public static String getTypeLabel(String type) {
        return switch (type) {
            case TYPE_LICENSE_EXPIRING -> "许可证即将到期";
            case TYPE_LICENSE_EXPIRED -> "许可证已过期";
            case TYPE_RECTIFICATION_OVERDUE -> "整改超期未反馈";
            case TYPE_INSPECTION_OVERDUE -> "检查任务超期";
            case TYPE_REPORT_MISSING -> "合规报告未提交";
            case TYPE_REGISTRATION_ANOMALY -> "注册备案异常";
            case TYPE_CREDIT_ANOMALY -> "信用异常";
            case TYPE_REPEAT_VIOLATION -> "屡次违规";
            default -> type;
        };
    }

    // ========== 预警等级中文名映射 ==========
    public static String getLevelLabel(String level) {
        return switch (level) {
            case LEVEL_HIGH -> "高";
            case LEVEL_MEDIUM -> "中";
            case LEVEL_LOW -> "低";
            default -> level;
        };
    }
}
