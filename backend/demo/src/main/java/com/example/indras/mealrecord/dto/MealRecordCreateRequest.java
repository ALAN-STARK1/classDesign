package com.example.indras.mealrecord.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import com.example.indras.mealrecord.dto.MealRecordItemDTO;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRecordCreateRequest {

    @jakarta.validation.constraints.NotNull
    private LocalDate recordDate;
    @jakarta.validation.constraints.NotBlank
    private String mealType;
    private String remark;
    @jakarta.validation.constraints.NotNull
    private List<MealRecordItemDTO> items;
}
