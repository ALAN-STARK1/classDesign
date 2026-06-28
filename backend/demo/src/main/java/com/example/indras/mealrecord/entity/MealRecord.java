package com.example.indras.mealrecord.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "meal_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "record_date")
    private LocalDate recordDate;
    @Column(name = "meal_type")
    private String mealType;
    @Column(name = "source_type")
    private String sourceType;
    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "total_calorie")
    private BigDecimal totalCalorie;
    @Column(name = "total_protein")
    private BigDecimal totalProtein;
    @Column(name = "total_fat")
    private BigDecimal totalFat;
    @Column(name = "total_carbohydrate")
    private BigDecimal totalCarbohydrate;
    private String remark;
}
