package com.example.indras.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacroRatioVO {

    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
}
