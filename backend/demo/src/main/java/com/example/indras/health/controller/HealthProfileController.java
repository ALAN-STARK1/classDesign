package com.example.indras.health.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.context.UserContext;
import com.example.indras.health.dto.AllergensUpdateRequest;
import com.example.indras.health.dto.HealthProfileSaveRequest;
import com.example.indras.health.dto.RestrictionsUpdateRequest;
import com.example.indras.health.service.HealthProfileService;
import com.example.indras.health.vo.HealthProfileSummaryVO;
import com.example.indras.health.vo.HealthProfileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health-profile")
@RequiredArgsConstructor
public class HealthProfileController {

    private final HealthProfileService healthProfileService;

    @GetMapping("/me")
    public ApiResponse<HealthProfileVO> getMyProfile() {
        return ApiResponse.success(healthProfileService.getMyProfile(UserContext.requireUserId()));
    }

    @PutMapping("/me")
    public ApiResponse<HealthProfileVO> saveMyProfile(@Valid @RequestBody HealthProfileSaveRequest request) {
        return ApiResponse.success(healthProfileService.saveMyProfile(UserContext.requireUserId(), request));
    }

    @GetMapping("/me/summary")
    public ApiResponse<HealthProfileSummaryVO> getMySummary() {
        return ApiResponse.success(healthProfileService.getMySummary(UserContext.requireUserId()));
    }

    @PutMapping("/me/allergens")
    public ApiResponse<List<String>> updateAllergens(@Valid @RequestBody AllergensUpdateRequest request) {
        return ApiResponse.success(healthProfileService.updateAllergens(UserContext.requireUserId(), request));
    }

    @PutMapping("/me/restrictions")
    public ApiResponse<List<String>> updateRestrictions(@Valid @RequestBody RestrictionsUpdateRequest request) {
        return ApiResponse.success(healthProfileService.updateRestrictions(UserContext.requireUserId(), request));
    }
}
