package com.example.indras.recipe.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.enums.RecipeStatus;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.NutritionCalculator;
import com.example.indras.common.util.PageUtils;
import com.example.indras.health.mapper.UserAllergenMapper;
import com.example.indras.health.entity.UserAllergen;
import com.example.indras.ingredient.entity.Ingredient;
import com.example.indras.ingredient.mapper.IngredientMapper;
import com.example.indras.recipe.dto.RecipeCreateRequest;
import com.example.indras.recipe.dto.RecipeIngredientBindRequest;
import com.example.indras.recipe.dto.RecipeIngredientItemDTO;
import com.example.indras.recipe.dto.RecipeStepDTO;
import com.example.indras.recipe.dto.SuitabilityRecalculateRequest;
import com.example.indras.recipe.entity.Recipe;
import com.example.indras.recipe.entity.RecipeFavorite;
import com.example.indras.recipe.entity.RecipeIngredient;
import com.example.indras.recipe.entity.RecipeStep;
import com.example.indras.recipe.entity.RecipeSuitabilityScore;
import com.example.indras.recipe.mapper.RecipeFavoriteMapper;
import com.example.indras.recipe.mapper.RecipeIngredientMapper;
import com.example.indras.recipe.mapper.RecipeMapper;
import com.example.indras.recipe.mapper.RecipeStepMapper;
import com.example.indras.recipe.mapper.RecipeSuitabilityScoreMapper;
import com.example.indras.recipe.service.RecipeService;
import com.example.indras.recipe.vo.RecipeDetailVO;
import com.example.indras.recipe.vo.RecipeIngredientVO;
import com.example.indras.recipe.vo.RecipeListItemVO;
import com.example.indras.recipe.vo.RecipeNutritionVO;
import com.example.indras.recipe.vo.RecipeStepVO;
import com.example.indras.recipe.vo.SuitabilityScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeMapper recipeMapper;
    private final RecipeIngredientMapper recipeIngredientMapper;
    private final RecipeStepMapper recipeStepMapper;
    private final RecipeFavoriteMapper recipeFavoriteMapper;
    private final RecipeSuitabilityScoreMapper recipeSuitabilityScoreMapper;
    private final IngredientMapper ingredientMapper;
    private final UserAllergenMapper userAllergenMapper;

    @Override
    public PageResult<RecipeListItemVO> page(Long userId, PageQuery query, String category,
                                             BigDecimal minCalorie, BigDecimal maxCalorie, String status) {
        String targetStatus = StringUtils.hasText(status) ? status : RecipeStatus.ONLINE.name();
        Page<Recipe> page = recipeMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<Recipe>lambdaQuery()
                        .eq(StringUtils.hasText(category), Recipe::getCategory, category)
                        .like(StringUtils.hasText(query.getKeyword()), Recipe::getName, query.getKeyword())
                        .ge(minCalorie != null, Recipe::getTotalCalorie, minCalorie)
                        .le(maxCalorie != null, Recipe::getTotalCalorie, maxCalorie)
                        .eq(Recipe::getStatus, targetStatus)
                        .orderByDesc(Recipe::getId));
        return PageUtils.toPageResult(page, recipe -> toListItem(userId, recipe));
    }

    @Override
    public RecipeDetailVO detail(Long userId, Long recipeId) {
        return toDetail(requireRecipe(recipeId), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecipeDetailVO create(Long userId, RecipeCreateRequest request) {
        Recipe recipe = Recipe.builder()
                .userId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .difficulty(request.getDifficulty() == null ? "NORMAL" : request.getDifficulty())
                .cookMinutes(request.getCookMinutes() == null ? 0 : request.getCookMinutes())
                .servings(request.getServings() == null ? 1 : request.getServings())
                .status(RecipeStatus.DRAFT.name())
                .totalCalorie(BigDecimal.ZERO)
                .totalProtein(BigDecimal.ZERO)
                .totalFat(BigDecimal.ZERO)
                .totalCarbohydrate(BigDecimal.ZERO)
                .build();
        recipeMapper.insert(recipe);
        saveSteps(recipe.getId(), request.getSteps());
        return toDetail(recipe, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecipeDetailVO update(Long userId, Long recipeId, RecipeCreateRequest request) {
        Recipe recipe = requireRecipe(recipeId);
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setCategory(request.getCategory());
        if (request.getDifficulty() != null) {
            recipe.setDifficulty(request.getDifficulty());
        }
        if (request.getCookMinutes() != null) {
            recipe.setCookMinutes(request.getCookMinutes());
        }
        if (request.getServings() != null) {
            recipe.setServings(request.getServings());
        }
        recipeMapper.updateById(recipe);
        recipeStepMapper.delete(Wrappers.<RecipeStep>lambdaQuery().eq(RecipeStep::getRecipeId, recipeId));
        saveSteps(recipeId, request.getSteps());
        return toDetail(recipe, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecipeDetailVO bindIngredients(Long userId, Long recipeId, RecipeIngredientBindRequest request) {
        Recipe recipe = requireRecipe(recipeId);
        recipeIngredientMapper.delete(Wrappers.<RecipeIngredient>lambdaQuery().eq(RecipeIngredient::getRecipeId, recipeId));
        for (RecipeIngredientItemDTO item : request.getIngredients()) {
            recipeIngredientMapper.insert(RecipeIngredient.builder()
                    .recipeId(recipeId)
                    .ingredientId(item.getIngredientId())
                    .amountG(item.getAmountG())
                    .remark(item.getRemark())
                    .build());
        }
        calculateNutrition(recipeId);
        return toDetail(recipeMapper.selectById(recipeId), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecipeNutritionVO calculateNutrition(Long recipeId) {
        requireRecipe(recipeId);
        NutritionCalculator.Nutrients total = NutritionCalculator.Nutrients.zero();
        List<RecipeIngredient> bindings = recipeIngredientMapper.selectList(
                Wrappers.<RecipeIngredient>lambdaQuery().eq(RecipeIngredient::getRecipeId, recipeId));
        for (RecipeIngredient binding : bindings) {
            Ingredient ingredient = ingredientMapper.selectById(binding.getIngredientId());
            if (ingredient != null) {
                total = NutritionCalculator.add(total, NutritionCalculator.fromIngredient(ingredient, binding.getAmountG()));
            }
        }
        Recipe recipe = recipeMapper.selectById(recipeId);
        recipe.setTotalCalorie(total.calorie());
        recipe.setTotalProtein(total.protein());
        recipe.setTotalFat(total.fat());
        recipe.setTotalCarbohydrate(total.carbohydrate());
        recipeMapper.updateById(recipe);
        return RecipeNutritionVO.builder()
                .recipeId(recipeId)
                .totalCalorie(total.calorie())
                .totalProtein(total.protein())
                .totalFat(total.fat())
                .totalCarbohydrate(total.carbohydrate())
                .build();
    }

    @Override
    public SuitabilityScoreVO suitability(Long userId, Long recipeId) {
        RecipeSuitabilityScore score = recipeSuitabilityScoreMapper.selectOne(
                Wrappers.<RecipeSuitabilityScore>lambdaQuery()
                        .eq(RecipeSuitabilityScore::getUserId, userId)
                        .eq(RecipeSuitabilityScore::getRecipeId, recipeId));
        if (score == null) {
            score = buildSuitabilityScore(userId, requireRecipe(recipeId));
            recipeSuitabilityScoreMapper.insert(score);
        }
        return toSuitabilityVo(recipeId, score);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recalculateSuitability(Long userId, SuitabilityRecalculateRequest request) {
        int updated = 0;
        for (Long recipeId : request.getRecipeIds()) {
            Recipe recipe = recipeMapper.selectById(recipeId);
            if (recipe == null) {
                continue;
            }
            RecipeSuitabilityScore score = buildSuitabilityScore(userId, recipe);
            RecipeSuitabilityScore existing = recipeSuitabilityScoreMapper.selectOne(
                    Wrappers.<RecipeSuitabilityScore>lambdaQuery()
                            .eq(RecipeSuitabilityScore::getUserId, userId)
                            .eq(RecipeSuitabilityScore::getRecipeId, recipeId));
            if (existing == null) {
                recipeSuitabilityScoreMapper.insert(score);
            } else {
                score.setId(existing.getId());
                recipeSuitabilityScoreMapper.updateById(score);
            }
            updated++;
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favorite(Long userId, Long recipeId) {
        requireRecipe(recipeId);
        Long count = recipeFavoriteMapper.selectCount(Wrappers.<RecipeFavorite>lambdaQuery()
                .eq(RecipeFavorite::getUserId, userId)
                .eq(RecipeFavorite::getRecipeId, recipeId));
        if (count != null && count > 0) {
            return true;
        }
        return recipeFavoriteMapper.insert(RecipeFavorite.builder()
                .userId(userId)
                .recipeId(recipeId)
                .createdAt(LocalDateTime.now())
                .build()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfavorite(Long userId, Long recipeId) {
        return recipeFavoriteMapper.delete(Wrappers.<RecipeFavorite>lambdaQuery()
                .eq(RecipeFavorite::getUserId, userId)
                .eq(RecipeFavorite::getRecipeId, recipeId)) > 0;
    }

    private Recipe requireRecipe(Long recipeId) {
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw BizException.notFound("菜谱不存在");
        }
        return recipe;
    }

    private void saveSteps(Long recipeId, List<RecipeStepDTO> steps) {
        if (steps == null) {
            return;
        }
        for (int i = 0; i < steps.size(); i++) {
            RecipeStepDTO step = steps.get(i);
            recipeStepMapper.insert(RecipeStep.builder()
                    .recipeId(recipeId)
                    .stepNo(step.getStepNo() == null ? i + 1 : step.getStepNo())
                    .content(step.getContent())
                    .build());
        }
    }

    private RecipeListItemVO toListItem(Long userId, Recipe recipe) {
        RecipeSuitabilityScore score = recipeSuitabilityScoreMapper.selectOne(
                Wrappers.<RecipeSuitabilityScore>lambdaQuery()
                        .eq(RecipeSuitabilityScore::getUserId, userId)
                        .eq(RecipeSuitabilityScore::getRecipeId, recipe.getId()));
        Long favCount = recipeFavoriteMapper.selectCount(Wrappers.<RecipeFavorite>lambdaQuery()
                .eq(RecipeFavorite::getUserId, userId)
                .eq(RecipeFavorite::getRecipeId, recipe.getId()));
        return RecipeListItemVO.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .category(recipe.getCategory())
                .difficulty(recipe.getDifficulty())
                .cookMinutes(recipe.getCookMinutes())
                .servings(recipe.getServings())
                .totalCalorie(recipe.getTotalCalorie())
                .totalProtein(recipe.getTotalProtein())
                .totalFat(recipe.getTotalFat())
                .totalCarbohydrate(recipe.getTotalCarbohydrate())
                .status(recipe.getStatus())
                .favorite(favCount != null && favCount > 0)
                .suitabilityScore(score == null ? null : score.getScore())
                .build();
    }

    private RecipeDetailVO toDetail(Recipe recipe, Long userId) {
        List<RecipeIngredientVO> ingredients = recipeIngredientMapper.selectList(
                        Wrappers.<RecipeIngredient>lambdaQuery().eq(RecipeIngredient::getRecipeId, recipe.getId()))
                .stream()
                .map(binding -> {
                    Ingredient ingredient = ingredientMapper.selectById(binding.getIngredientId());
                    return RecipeIngredientVO.builder()
                            .ingredientId(binding.getIngredientId())
                            .name(ingredient == null ? null : ingredient.getName())
                            .amountG(binding.getAmountG())
                            .remark(binding.getRemark())
                            .build();
                }).toList();
        List<RecipeStepVO> steps = recipeStepMapper.selectList(
                        Wrappers.<RecipeStep>lambdaQuery().eq(RecipeStep::getRecipeId, recipe.getId())
                                .orderByAsc(RecipeStep::getStepNo))
                .stream()
                .map(step -> RecipeStepVO.builder().stepNo(step.getStepNo()).content(step.getContent()).build())
                .toList();
        return RecipeDetailVO.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .category(recipe.getCategory())
                .difficulty(recipe.getDifficulty())
                .cookMinutes(recipe.getCookMinutes())
                .servings(recipe.getServings())
                .totalCalorie(recipe.getTotalCalorie())
                .totalProtein(recipe.getTotalProtein())
                .totalFat(recipe.getTotalFat())
                .totalCarbohydrate(recipe.getTotalCarbohydrate())
                .status(recipe.getStatus())
                .ingredients(ingredients)
                .steps(steps)
                .build();
    }

    private RecipeSuitabilityScore buildSuitabilityScore(Long userId, Recipe recipe) {
        int calorieScore = 85;
        int macroScore = 80;
        int preferenceScore = 75;
        int riskScore = 100;
        if (containsAllergen(userId, recipe.getId())) {
            riskScore = 0;
        }
        int total = (int) Math.round(calorieScore * 0.30 + macroScore * 0.25 + preferenceScore * 0.20 + 85 * 0.15 + riskScore * 0.10);
        return RecipeSuitabilityScore.builder()
                .userId(userId)
                .recipeId(recipe.getId())
                .score(total)
                .calorieScore(calorieScore)
                .macroScore(macroScore)
                .preferenceScore(preferenceScore)
                .riskScore(riskScore)
                .reason(riskScore == 0 ? "包含过敏食材，不建议选择" : "综合营养与目标匹配")
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    private boolean containsAllergen(Long userId, Long recipeId) {
        List<String> allergens = userAllergenMapper.selectList(Wrappers.<UserAllergen>lambdaQuery().eq(UserAllergen::getUserId, userId))
                .stream().map(UserAllergen::getAllergenName).toList();
        if (allergens.isEmpty()) {
            return false;
        }
        List<RecipeIngredient> bindings = recipeIngredientMapper.selectList(
                Wrappers.<RecipeIngredient>lambdaQuery().eq(RecipeIngredient::getRecipeId, recipeId));
        for (RecipeIngredient binding : bindings) {
            Ingredient ingredient = ingredientMapper.selectById(binding.getIngredientId());
            if (ingredient != null && allergens.stream().anyMatch(a -> ingredient.getName().contains(a))) {
                return true;
            }
        }
        return false;
    }

    private SuitabilityScoreVO toSuitabilityVo(Long recipeId, RecipeSuitabilityScore score) {
        return SuitabilityScoreVO.builder()
                .recipeId(recipeId)
                .score(score.getScore())
                .calorieScore(score.getCalorieScore())
                .macroScore(score.getMacroScore())
                .preferenceScore(score.getPreferenceScore())
                .riskScore(score.getRiskScore())
                .reason(score.getReason())
                .build();
    }
}
