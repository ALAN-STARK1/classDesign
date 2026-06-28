package com.example.indras.health.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllergensUpdateRequest {

    @jakarta.validation.constraints.NotNull
    private List<String> allergens;
}
