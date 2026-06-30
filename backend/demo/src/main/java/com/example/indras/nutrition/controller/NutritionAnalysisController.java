package com.example.indras.nutrition.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.context.UserContext;
import com.example.indras.nutrition.service.NutritionAnalysisService;
import com.example.indras.nutrition.vo.NutritionGapVO;
import com.example.indras.nutrition.vo.NutritionRangeVO;
import com.example.indras.nutrition.vo.TodayNutritionVO;
import com.example.indras.risk.vo.NutritionRiskResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
public class NutritionAnalysisController {

    private final NutritionAnalysisService nutritionAnalysisService;

    @GetMapping("/today")
    public ApiResponse<TodayNutritionVO> today(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(nutritionAnalysisService.today(UserContext.requireUserId(), date));
    }

    @GetMapping("/range")
    public ApiResponse<NutritionRangeVO> range(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(nutritionAnalysisService.range(UserContext.requireUserId(), startDate, endDate));
    }

    @GetMapping("/gap")
    public ApiResponse<NutritionGapVO> gap(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date == null ? LocalDate.now() : date;
        return ApiResponse.success(nutritionAnalysisService.gap(UserContext.requireUserId(), target));
    }

    @GetMapping("/risks")
    public ApiResponse<List<NutritionRiskResultVO>> risks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(nutritionAnalysisService.risks(UserContext.requireUserId(), startDate, endDate));
    }
}
