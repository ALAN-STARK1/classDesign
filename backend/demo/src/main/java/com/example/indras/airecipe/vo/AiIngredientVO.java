package com.example.indras.airecipe.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiIngredientVO {

    private String name;
    private BigDecimal amount;
    private String unit;
}
