package com.example.indras.risk.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.risk.dto.NutritionRiskRuleSaveRequest;
import com.example.indras.risk.dto.RiskEvaluateRequest;
import com.example.indras.risk.dto.RiskRuleStatusRequest;
import com.example.indras.risk.service.NutritionRiskRuleService;
import com.example.indras.risk.vo.NutritionRiskResultVO;
import com.example.indras.risk.vo.NutritionRiskRuleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nutrition-risk-rules")
@RequiredArgsConstructor
public class NutritionRiskRuleController {

    private final NutritionRiskRuleService nutritionRiskRuleService;

    @GetMapping
    public ApiResponse<PageResult<NutritionRiskRuleVO>> page(PageQuery query,
                                                             @RequestParam(required = false) String scenario) {
        return ApiResponse.success(nutritionRiskRuleService.page(query, scenario));
    }

    @PostMapping
    public ApiResponse<NutritionRiskRuleVO> create(@Valid @RequestBody NutritionRiskRuleSaveRequest request) {
        return ApiResponse.success(nutritionRiskRuleService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<NutritionRiskRuleVO> update(@PathVariable Long id,
                                                   @Valid @RequestBody NutritionRiskRuleSaveRequest request) {
        return ApiResponse.success(nutritionRiskRuleService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<NutritionRiskRuleVO> updateStatus(@PathVariable Long id,
                                                         @Valid @RequestBody RiskRuleStatusRequest request) {
        return ApiResponse.success(nutritionRiskRuleService.updateStatus(id, request));
    }

    @PostMapping("/evaluate")
    public ApiResponse<List<NutritionRiskResultVO>> evaluate(@Valid @RequestBody RiskEvaluateRequest request) {
        return ApiResponse.success(nutritionRiskRuleService.evaluate(UserContext.requireUserId(), request));
    }
}
