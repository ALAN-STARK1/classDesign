package com.example.indras.airecipe.controller;

import com.example.indras.airecipe.dto.AiRecipeParseRequest;
import com.example.indras.airecipe.dto.AiRecipeToMealRecordRequest;
import com.example.indras.airecipe.service.AiRecipeService;
import com.example.indras.airecipe.vo.AiRecipeListItemVO;
import com.example.indras.airecipe.vo.AiRecipeVO;
import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.mealrecord.vo.MealRecordVO;
import com.example.indras.recipe.vo.RecipeDetailVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai-recipes")
@RequiredArgsConstructor
public class AiRecipeController {

    private final AiRecipeService aiRecipeService;

    @PostMapping("/parse")
    public ApiResponse<AiRecipeVO> parseText(@Valid @RequestBody AiRecipeParseRequest request) {
        return ApiResponse.success(aiRecipeService.parseText(UserContext.requireUserId(), request));
    }

    @PostMapping("/parse-image")
    public ApiResponse<AiRecipeVO> parseImage(@RequestParam("file") MultipartFile file,
                                              @RequestParam(required = false) String prompt) {
        return ApiResponse.success(aiRecipeService.parseImage(UserContext.requireUserId(), file, prompt));
    }

    @GetMapping("/history")
    public ApiResponse<PageResult<AiRecipeListItemVO>> history(PageQuery query,
                                                               @RequestParam(required = false) String sourceType) {
        return ApiResponse.success(aiRecipeService.history(UserContext.requireUserId(), query, sourceType));
    }

    @GetMapping("/{id}")
    public ApiResponse<AiRecipeVO> detail(@PathVariable Long id) {
        return ApiResponse.success(aiRecipeService.detail(UserContext.requireUserId(), id));
    }

    @PatchMapping("/{id}/confirm")
    public ApiResponse<AiRecipeVO> confirm(@PathVariable Long id,
                                           @RequestBody(required = false) Map<String, Object> payload) {
        return ApiResponse.success(aiRecipeService.confirm(UserContext.requireUserId(), id, payload));
    }

    @PostMapping("/{id}/to-recipe")
    public ApiResponse<RecipeDetailVO> toRecipe(@PathVariable Long id) {
        return ApiResponse.success(aiRecipeService.toRecipe(UserContext.requireUserId(), id));
    }

    @PostMapping("/{id}/to-meal-record")
    public ApiResponse<MealRecordVO> toMealRecord(@PathVariable Long id,
                                                  @Valid @RequestBody AiRecipeToMealRecordRequest request) {
        return ApiResponse.success(aiRecipeService.toMealRecord(UserContext.requireUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(aiRecipeService.delete(UserContext.requireUserId(), id));
    }
}
