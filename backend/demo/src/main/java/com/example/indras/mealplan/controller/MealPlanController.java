package com.example.indras.mealplan.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.context.UserContext;
import com.example.indras.common.vo.CountResultVO;
import com.example.indras.mealplan.dto.MealPlanFeedbackRequest;
import com.example.indras.mealplan.dto.MealPlanGenerateRequest;
import com.example.indras.mealplan.dto.MealPlanReplaceRequest;
import com.example.indras.mealplan.dto.MealPlanToRecordsRequest;
import com.example.indras.mealplan.service.MealPlanService;
import com.example.indras.mealplan.vo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PostMapping("/generate/day")
    public ApiResponse<MealPlanVO> generateDayPlan(@Valid @RequestBody MealPlanGenerateRequest request) {
        return ApiResponse.success(mealPlanService.generateDayPlan(UserContext.requireUserId(), request));
    }

    @GetMapping("/day")
    public ApiResponse<MealPlanVO> getDayPlan(
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate) {
        return ApiResponse.success(mealPlanService.getDayPlan(UserContext.requireUserId(), planDate));
    }

    @GetMapping("/{planId}/items/{itemId}/replacement-candidates")
    public ApiResponse<List<ReplacementCandidateVO>> replacementCandidates(
            @PathVariable Long planId,
            @PathVariable Long itemId,
            @RequestParam(required = false) String reason,
            @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(mealPlanService.replacementCandidates(
                UserContext.requireUserId(), planId, itemId, reason, limit));
    }

    @PatchMapping("/{planId}/items/{itemId}/replace")
    public ApiResponse<MealPlanVO> replaceItem(@PathVariable Long planId,
                                               @PathVariable Long itemId,
                                               @Valid @RequestBody MealPlanReplaceRequest request) {
        return ApiResponse.success(mealPlanService.replaceItem(UserContext.requireUserId(), planId, itemId, request));
    }

    @GetMapping("/{planId}/replace-logs")
    public ApiResponse<List<MealPlanReplaceLogVO>> replaceLogs(@PathVariable Long planId) {
        return ApiResponse.success(mealPlanService.replaceLogs(UserContext.requireUserId(), planId));
    }

    @PostMapping("/{planId}/items/{itemId}/feedback")
    public ApiResponse<MealPlanFeedbackVO> submitFeedback(@PathVariable Long planId,
                                                          @PathVariable Long itemId,
                                                          @Valid @RequestBody MealPlanFeedbackRequest request) {
        return ApiResponse.success(mealPlanService.submitFeedback(
                UserContext.requireUserId(), planId, itemId, request));
    }

    @GetMapping("/{planId}/feedback")
    public ApiResponse<List<MealPlanFeedbackVO>> listFeedback(@PathVariable Long planId) {
        return ApiResponse.success(mealPlanService.listFeedback(UserContext.requireUserId(), planId));
    }

    @GetMapping("/{planId}/completion")
    public ApiResponse<MealPlanCompletionVO> completion(@PathVariable Long planId) {
        return ApiResponse.success(mealPlanService.completion(UserContext.requireUserId(), planId));
    }

    @PostMapping("/{planId}/to-records")
    public ApiResponse<CountResultVO> toMealRecords(@PathVariable Long planId,
                                                    @Valid @RequestBody MealPlanToRecordsRequest request) {
        return ApiResponse.success(mealPlanService.toMealRecords(UserContext.requireUserId(), planId, request));
    }

    @GetMapping("/{planId}/shopping-list")
    public ApiResponse<ShoppingListVO> shoppingList(@PathVariable Long planId) {
        return ApiResponse.success(mealPlanService.shoppingList(UserContext.requireUserId(), planId));
    }
}
