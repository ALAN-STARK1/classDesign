package com.example.indras.admin.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    private Boolean approved;
    private String reason;
}
