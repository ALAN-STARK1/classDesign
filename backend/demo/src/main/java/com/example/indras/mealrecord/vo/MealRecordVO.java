package com.example.indras.mealrecord.vo;

import lombok.*;
import com.example.indras.mealrecord.vo.MealRecordItemVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRecordVO {

    private Long id;
    private LocalDate recordDate;
    private String mealType;
    private String sourceType;
    private Long sourceId;
    private BigDecimal totalCalorie;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalCarbohydrate;
    private String remark;
    private List<MealRecordItemVO> items;
}
