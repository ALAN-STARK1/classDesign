package com.example.indras.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String method;
    private String path;
    private String ip;
    @Column(name = "elapsed_ms")
    private Integer elapsedMs;
    @Column(name = "status_code")
    private Integer statusCode;
    @Column(name = "biz_code")
    private Integer bizCode;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
