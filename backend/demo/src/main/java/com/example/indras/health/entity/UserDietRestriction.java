package com.example.indras.health.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_diet_restriction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDietRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "restriction_name")
    private String restrictionName;
}
