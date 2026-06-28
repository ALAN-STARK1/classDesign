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
    private String scene;
    private String requestSummary;
    private Integer elapsedMs;
    private Boolean success;
    private String errorMessage;
    private OffsetDateTime createdAt;
}
