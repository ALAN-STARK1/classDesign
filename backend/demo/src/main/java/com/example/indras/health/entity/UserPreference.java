package com.example.indras.health.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "liked_tags")
    private String likedTags;
    @Column(name = "disliked_ingredients")
    private String dislikedIngredients;
    @Column(name = "preferred_cuisines")
    private String preferredCuisines;
    @Column(name = "spice_level")
    private String spiceLevel;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
