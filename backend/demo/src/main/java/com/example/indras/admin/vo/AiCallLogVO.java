package com.example.indras.admin.vo;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCallLogVO {

    private Long id;
    private Long userId;
    private String callerName;
    private String scene;
    private String model;
    private String endpoint;
    private String requestSummary;
    private Integer elapsedMs;
    private Integer latencyMs;
    private Integer inputTokens;
    private Integer outputTokens;
    private Boolean success;
    private String status;
    private String errorMessage;
    private OffsetDateTime createdAt;
}
