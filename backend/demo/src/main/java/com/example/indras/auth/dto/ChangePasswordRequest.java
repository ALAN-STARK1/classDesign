package com.example.indras.auth.dto;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @jakarta.validation.constraints.NotBlank
    private String oldPassword;
    @jakarta.validation.constraints.NotBlank
    @Size(min = 6, max = 32)
    private String newPassword;
}
