package com.example.indras.airecipe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "source_type")
    private String sourceType;
    @Column(name = "recipe_name")
    private String recipeName;
    private String description;
    @Column(name = "recognized_foods_json")
    @Lob
    private String recognizedFoodsJson;
    @Column(name = "ingredients_json")
    @Lob
    private String ingredientsJson;
    @Column(name = "nutrition_json")
    @Lob
    private String nutritionJson;
    @Column(name = "suitability_score")
    private Integer suitabilityScore;
    @Column(name = "suitability_reason")
    private String suitabilityReason;
    @Column(name = "health_tips_json")
    @Lob
    private String healthTipsJson;
    @Column(name = "warnings_json")
    @Lob
    private String warningsJson;
    @Column(name = "raw_response_json")
    @Lob
    private String rawResponseJson;
    @Column(name = "source_image_url")
    private String sourceImageUrl;
    @Column(name = "source_image_key")
    private String sourceImageKey;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
