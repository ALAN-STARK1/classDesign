package com.example.indras.airecipe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_call_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String scene;
    @Column(name = "request_summary")
    private String requestSummary;
    @Column(name = "elapsed_ms")
    private Integer elapsedMs;
    private Boolean success;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
