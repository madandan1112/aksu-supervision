package cn.aksu.supervision.message.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.message.entity.Message;
import cn.aksu.supervision.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

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

    public void updateSettings(Long userId, String settings) {
        // 消息设置存储逻辑，可扩展为独立配置表
    }

    public Message sendMessage(Long userId, String type, String title, String content, String link, Long relatedId) {
        Message message = Message.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .content(content)
                .link(link)
                .relatedId(relatedId)
                .isRead(false)
                .build();
        return messageRepository.save(message);
    }
}
