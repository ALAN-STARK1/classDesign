package com.example.indras.airecipe.service;

import com.example.indras.airecipe.dto.AiRecipeParseRequest;
import com.example.indras.airecipe.dto.AiRecipeGenerateRequest;
import com.example.indras.airecipe.dto.AiRecipeToMealRecordRequest;
import com.example.indras.airecipe.vo.AiRecipeListItemVO;
import com.example.indras.airecipe.vo.AiRecipeVO;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.mealrecord.vo.MealRecordVO;
import com.example.indras.recipe.vo.RecipeDetailVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AiRecipeService {

    AiRecipeVO parseText(Long userId, AiRecipeParseRequest request);

    AiRecipeVO generate(Long userId, AiRecipeGenerateRequest request);

    AiRecipeVO parseImage(Long userId, MultipartFile file, String prompt);

    AiRecipeVO detail(Long userId, Long id);

    PageResult<AiRecipeListItemVO> history(Long userId, PageQuery query, String sourceType);

    AiRecipeVO confirm(Long userId, Long id, Map<String, Object> payload);

    RecipeDetailVO toRecipe(Long userId, Long id);

    MealRecordVO toMealRecord(Long userId, Long id, AiRecipeToMealRecordRequest request);

    boolean delete(Long userId, Long id);
}
