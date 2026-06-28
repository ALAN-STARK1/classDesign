package com.example.indras.report.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "report_type")
    private String reportType;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "avg_calorie")
    private BigDecimal avgCalorie;
    @Column(name = "avg_protein")
    private BigDecimal avgProtein;
    @Column(name = "avg_fat")
    private BigDecimal avgFat;
    @Column(name = "avg_carbohydrate")
    private BigDecimal avgCarbohydrate;
    @Column(name = "target_days")
    private Integer targetDays;
    @Column(name = "record_days")
    private Integer recordDays;
    @Column(name = "completion_rate")
    private BigDecimal completionRate;
    @Column(name = "risk_count")
    private Integer riskCount;
    private String summary;
    @Column(name = "suggestions_json")
    @Lob
    private String suggestionsJson;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
