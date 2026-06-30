package com.example.indras.recipe.service;

import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.recipe.dto.RecipeCreateRequest;
import com.example.indras.recipe.dto.RecipeIngredientBindRequest;
import com.example.indras.recipe.dto.SuitabilityRecalculateRequest;
import com.example.indras.recipe.vo.RecipeDetailVO;
import com.example.indras.recipe.vo.RecipeListItemVO;
import com.example.indras.recipe.vo.RecipeNutritionVO;
import com.example.indras.recipe.vo.SuitabilityScoreVO;

import java.math.BigDecimal;

public interface RecipeService {

    PageResult<RecipeListItemVO> page(Long userId, PageQuery query, String category,
                                       BigDecimal minCalorie, BigDecimal maxCalorie, String status);

    RecipeDetailVO detail(Long userId, Long recipeId);

    RecipeDetailVO create(Long userId, RecipeCreateRequest request);

    RecipeDetailVO update(Long userId, Long recipeId, RecipeCreateRequest request);

    RecipeDetailVO bindIngredients(Long userId, Long recipeId, RecipeIngredientBindRequest request);

    RecipeNutritionVO calculateNutrition(Long recipeId);

    SuitabilityScoreVO suitability(Long userId, Long recipeId);

    int recalculateSuitability(Long userId, SuitabilityRecalculateRequest request);

    boolean favorite(Long userId, Long recipeId);

    boolean unfavorite(Long userId, Long recipeId);
}
