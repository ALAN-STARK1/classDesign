package com.example.indras.mealrecord.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.context.UserContext;
import com.example.indras.mealrecord.dto.MealRecordCreateRequest;
import com.example.indras.mealrecord.dto.MealRecordFromSourceRequest;
import com.example.indras.mealrecord.service.MealRecordService;
import com.example.indras.mealrecord.vo.DailyMealRecordVO;
import com.example.indras.mealrecord.vo.MealRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/meal-records")
@RequiredArgsConstructor
public class MealRecordController {

    private final MealRecordService mealRecordService;

    @PostMapping
    public ApiResponse<MealRecordVO> createManual(@Valid @RequestBody MealRecordCreateRequest request) {
        return ApiResponse.success(mealRecordService.createManual(UserContext.requireUserId(), request));
    }

    @PostMapping("/from-recipe/{recipeId}")
    public ApiResponse<MealRecordVO> fromRecipe(@PathVariable Long recipeId,
                                                @Valid @RequestBody MealRecordFromSourceRequest request) {
        return ApiResponse.success(mealRecordService.fromRecipe(UserContext.requireUserId(), recipeId, request));
    }

    @GetMapping("/day")
    public ApiResponse<DailyMealRecordVO> getDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(mealRecordService.getDay(UserContext.requireUserId(), date));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(mealRecordService.delete(UserContext.requireUserId(), id));
    }
}
