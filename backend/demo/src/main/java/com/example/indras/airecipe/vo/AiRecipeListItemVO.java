package com.example.indras.airecipe.vo;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecipeListItemVO {

    private Long id;
    private String recipeName;
    private String sourceType;
    private String status;
    private Integer suitabilityScore;
    private BigDecimal totalCalorie;
    private OffsetDateTime createdAt;
}
