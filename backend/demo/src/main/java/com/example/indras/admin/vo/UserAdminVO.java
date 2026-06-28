package com.example.indras.admin.vo;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminVO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
    private OffsetDateTime createdAt;
}
