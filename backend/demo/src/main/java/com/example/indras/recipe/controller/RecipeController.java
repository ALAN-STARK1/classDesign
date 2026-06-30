package com.example.indras.recipe.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.recipe.dto.RecipeCreateRequest;
import com.example.indras.recipe.dto.RecipeIngredientBindRequest;
import com.example.indras.recipe.dto.SuitabilityRecalculateRequest;
import com.example.indras.recipe.service.RecipeService;
import com.example.indras.recipe.vo.RecipeDetailVO;
import com.example.indras.recipe.vo.RecipeListItemVO;
import com.example.indras.recipe.vo.RecipeNutritionVO;
import com.example.indras.recipe.vo.SuitabilityScoreVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ApiResponse<PageResult<RecipeListItemVO>> page(PageQuery query,
                                                          @RequestParam(required = false) String category,
                                                          @RequestParam(required = false) BigDecimal minCalorie,
                                                          @RequestParam(required = false) BigDecimal maxCalorie,
                                                          @RequestParam(required = false) String status) {
        return ApiResponse.success(recipeService.page(
                UserContext.requireUserId(), query, category, minCalorie, maxCalorie, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<RecipeDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(recipeService.detail(UserContext.requireUserId(), id));
    }

    @PostMapping
    public ApiResponse<RecipeDetailVO> create(@Valid @RequestBody RecipeCreateRequest request) {
        return ApiResponse.success(recipeService.create(UserContext.requireUserId(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RecipeDetailVO> update(@PathVariable Long id,
                                              @Valid @RequestBody RecipeCreateRequest request) {
        return ApiResponse.success(recipeService.update(UserContext.requireUserId(), id, request));
    }

    @PutMapping("/{id}/ingredients")
    public ApiResponse<RecipeDetailVO> bindIngredients(@PathVariable Long id,
                                                       @Valid @RequestBody RecipeIngredientBindRequest request) {
        return ApiResponse.success(recipeService.bindIngredients(UserContext.requireUserId(), id, request));
    }

    @PostMapping("/{id}/nutrition/calculate")
    public ApiResponse<RecipeNutritionVO> calculateNutrition(@PathVariable Long id) {
        return ApiResponse.success(recipeService.calculateNutrition(id));
    }

    @GetMapping("/{id}/suitability")
    public ApiResponse<SuitabilityScoreVO> suitability(@PathVariable Long id) {
        return ApiResponse.success(recipeService.suitability(UserContext.requireUserId(), id));
    }

    @PostMapping("/suitability/recalculate")
    public ApiResponse<Integer> recalculateSuitability(@Valid @RequestBody SuitabilityRecalculateRequest request) {
        return ApiResponse.success(recipeService.recalculateSuitability(UserContext.requireUserId(), request));
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<Boolean> favorite(@PathVariable Long id) {
        return ApiResponse.success(recipeService.favorite(UserContext.requireUserId(), id));
    }

    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Boolean> unfavorite(@PathVariable Long id) {
        return ApiResponse.success(recipeService.unfavorite(UserContext.requireUserId(), id));
    }
}
