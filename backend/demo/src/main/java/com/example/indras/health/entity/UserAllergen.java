package com.example.indras.health.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_allergen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAllergen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "allergen_name")
    private String allergenName;
}
