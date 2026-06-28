package com.example.indras.auth.dto;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @jakarta.validation.constraints.NotBlank
    @Size(min = 4, max = 50)
    private String username;
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Email
    private String email;
    @jakarta.validation.constraints.NotBlank
    @Size(min = 6, max = 32)
    private String password;
}
