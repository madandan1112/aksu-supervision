package cn.aksu.supervision.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_operation_log")
public class SysOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String username;

    @Column(length = 20)
    private String operation;

    @Column(length = 200)
    private String method;

    @Column(length = 200)
    private String url;

    @Column(length = 20)
    private String httpMethod;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(length = 20)
    private String ip;

    @Column
    private Long duration;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;
}
