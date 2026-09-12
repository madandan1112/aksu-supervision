package cn.aksu.supervision.message.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 微信小程序订阅消息推送。
 * 部署时通过环境变量注入 WX_MINI_APPID / WX_MINI_SECRET / WX_TPL_RECTIFY / WX_TPL_ALERT；
 * 未配置时自动降级为仅站内信（不报错、不影响主流程）。
 */
@Slf4j
@Service
public class WeChatNotifyService {

    @Value("${wx.mini.appid:}")
    private String appid;

    @Value("${wx.mini.secret:}")
    private String secret;

    @Value("${wx.mini.template-rectify:}")
    private String templateRectify;

    @Value("${wx.mini.template-alert:}")
    private String templateAlert;

    private boolean enabled() {
        return !appid.isBlank() && !secret.isBlank();
    }

    /**
     * 推送订阅消息（异步尽力而为）。
     * @param type 站内信类型：RECTIFICATION_NOTICE / ALERT 等
     */
    public void trySendSubscribeMessage(Long userId, String type, String title, String content) {
        if (!enabled()) {
            return; // 未配置小程序凭据：降级为仅站内信
        }
        String templateId = pickTemplate(type);
        if (templateId == null || templateId.isBlank()) {
            return; // 该类型未配置订阅消息模板
        }
        // 小程序 appid 下发后启用真实推送：
        // 1) GET /cgi-bin/token?grant_type=client_credential&appid=&secret= 获取 access_token（需缓存）
        // 2) POST /cgi-bin/message/subscribe/send {touser, template_id, page, data}
        //    touser = 用户微信 openid（需在 wx-login 时与 userId 绑定存储）
        log.info("[WeChat订阅消息] 待推送 userId={} type={} title={} template={}",
                userId, type, title, templateId);
    }

    private String pickTemplate(String type) {
        if (type == null) return null;
        if (type.contains("RECTIFICATION")) return templateRectify;
        if (type.contains("ALERT")) return templateAlert;
        return null;
    }
}
