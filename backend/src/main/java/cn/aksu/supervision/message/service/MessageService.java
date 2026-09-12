package cn.aksu.supervision.message.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.message.entity.Message;
import cn.aksu.supervision.message.repository.MessageRepository;
import cn.aksu.supervision.system.entity.SysConfig;
import cn.aksu.supervision.system.repository.SysConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private static final String SETTINGS_KEY_PREFIX = "message.settings.";

    private final MessageRepository messageRepository;
    private final SysConfigRepository sysConfigRepository;
    private final WeChatNotifyService weChatNotifyService;

    @Transactional(readOnly = true)
    public PageResult<Message> listMessages(Long userId, String type, int page, int size) {
        Page<Message> pageData;
        if (type != null) {
            pageData = messageRepository.findByUserIdAndTypeOrderByCreateTimeDesc(userId, type,
                    PageRequest.of(page - 1, size));
        } else {
            pageData = messageRepository.findByUserIdOrderByCreateTimeDesc(userId,
                    PageRequest.of(page - 1, size));
        }
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public Message markAsRead(Long id, Long userId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("消息不存在"));

        if (!message.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此消息");
        }

        message.setIsRead(true);
        return messageRepository.save(message);
    }

    public void deleteMessage(Long id, Long userId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("消息不存在"));

        if (!message.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此消息");
        }

        messageRepository.delete(message);
    }

    /** 消息设置持久化到 sys_config（key=message.settings.{userId}，group=message） */
    public void updateSettings(Long userId, String settings) {
        if (settings == null || settings.isBlank()) {
            return;
        }
        String key = SETTINGS_KEY_PREFIX + userId;
        SysConfig config = sysConfigRepository.findByConfigKey(key).orElse(null);
        if (config == null) {
            config = SysConfig.builder()
                    .configKey(key)
                    .configGroup("message")
                    .description("用户消息设置 userId=" + userId)
                    .build();
        }
        config.setConfigValue(settings);
        config.setUpdateTime(LocalDateTime.now());
        sysConfigRepository.save(config);
    }

    @Transactional(readOnly = true)
    public String getSettings(Long userId) {
        return sysConfigRepository.findByConfigKey(SETTINGS_KEY_PREFIX + userId)
                .map(SysConfig::getConfigValue)
                .filter(v -> !v.isBlank())
                .orElse("{}");
    }

    public Message sendMessage(Long userId, String type, String title, String content, String link, Long relatedId) {
        return sendMessage(userId, "enterprise", type, title, content, link, relatedId);
    }

    public Message sendMessage(Long userId, String userType, String type, String title, String content, String link, Long relatedId) {
        Message message = Message.builder()
                .userId(userId)
                .userType(userType)
                .type(type)
                .title(title)
                .content(content)
                .link(link)
                .relatedId(relatedId)
                .isRead(false)
                .build();
        Message saved = messageRepository.save(message);
        // 尽力而为的微信订阅消息推送（未配置小程序凭据时自动降级为仅站内信）
        try {
            weChatNotifyService.trySendSubscribeMessage(userId, type, title, content);
        } catch (Exception e) {
            log.warn("订阅消息推送失败（不影响站内信）: {}", e.getMessage());
        }
        return saved;
    }
}
