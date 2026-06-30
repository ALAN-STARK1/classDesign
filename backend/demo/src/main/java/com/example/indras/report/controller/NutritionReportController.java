package com.example.indras.report.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.report.dto.MonthlyReportRequest;
import com.example.indras.report.dto.WeeklyReportRequest;
import com.example.indras.report.service.NutritionReportService;
import com.example.indras.report.vo.NutritionReportVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nutrition-reports")
@RequiredArgsConstructor
public class NutritionReportController {

    private final NutritionReportService nutritionReportService;

    @PostMapping("/weekly")
    public ApiResponse<NutritionReportVO> generateWeekly(@Valid @RequestBody WeeklyReportRequest request) {
        return ApiResponse.success(nutritionReportService.generateWeekly(UserContext.requireUserId(), request));
    }

    @PostMapping("/monthly")
    public ApiResponse<NutritionReportVO> generateMonthly(@Valid @RequestBody MonthlyReportRequest request) {
        return ApiResponse.success(nutritionReportService.generateMonthly(UserContext.requireUserId(), request));
    }

    @GetMapping
    public ApiResponse<PageResult<NutritionReportVO>> page(PageQuery query) {
        return ApiResponse.success(nutritionReportService.page(UserContext.requireUserId(), query));
    }

    @GetMapping("/{id}")
    public ApiResponse<NutritionReportVO> detail(@PathVariable Long id) {
        return ApiResponse.success(nutritionReportService.detail(UserContext.requireUserId(), id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(nutritionReportService.delete(UserContext.requireUserId(), id));
    }
}
