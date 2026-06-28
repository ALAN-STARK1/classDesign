package com.example.indras.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String type;
    private String title;
    private String content;
    @Column(name = "read_flag")
    private Boolean readFlag;
    @Column(name = "related_type")
    private String relatedType;
    @Column(name = "related_id")
    private Long relatedId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
