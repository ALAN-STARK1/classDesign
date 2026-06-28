package com.example.indras.auth.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseVO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
}
