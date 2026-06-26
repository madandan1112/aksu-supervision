package cn.aksu.supervision.message.repository;

import cn.aksu.supervision.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    Page<Message> findByUserIdAndTypeOrderByCreateTimeDesc(Long userId, String type, Pageable pageable);

    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}
