package com.example.indras.ingredient.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.enums.UserStatus;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.PageUtils;
import com.example.indras.ingredient.dto.IngredientSaveRequest;
import com.example.indras.ingredient.entity.Ingredient;
import com.example.indras.ingredient.mapper.IngredientMapper;
import com.example.indras.ingredient.service.IngredientService;
import com.example.indras.ingredient.vo.IngredientVO;
import com.example.indras.health.entity.UserAllergen;
import com.example.indras.health.mapper.UserAllergenMapper;
import com.example.indras.ingredient.vo.IngredientPairingVO;
import com.example.indras.recipe.entity.RecipeIngredient;
import com.example.indras.recipe.mapper.RecipeIngredientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientMapper ingredientMapper;
    private final RecipeIngredientMapper recipeIngredientMapper;
    private final UserAllergenMapper userAllergenMapper;

    @Override
    public PageResult<IngredientVO> page(PageQuery query, String category) {
        Page<Ingredient> page = ingredientMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<Ingredient>lambdaQuery()
                        .eq(StringUtils.hasText(category), Ingredient::getCategory, category)
                        .like(StringUtils.hasText(query.getKeyword()), Ingredient::getName, query.getKeyword())
                        .orderByAsc(Ingredient::getId));
        return PageUtils.toPageResult(page, this::toVo);
    }

    @Override
    public IngredientVO getById(Long id) {
        Ingredient ingredient = requireIngredient(id);
        return toVo(ingredient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngredientVO create(IngredientSaveRequest request) {
        Ingredient ingredient = fromRequest(request);
        ingredient.setStatus(UserStatus.ENABLED.name());
        ingredientMapper.insert(ingredient);
        return toVo(ingredient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngredientVO update(Long id, IngredientSaveRequest request) {
        Ingredient ingredient = requireIngredient(id);
        applyRequest(ingredient, request);
        ingredientMapper.updateById(ingredient);
        return toVo(ingredient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngredientVO disable(Long id) {
        Ingredient ingredient = requireIngredient(id);
        ingredient.setStatus(UserStatus.DISABLED.name());
        ingredientMapper.updateById(ingredient);
        return toVo(ingredient);
    }

    @Override
    public List<IngredientPairingVO> pairings(Long userId, Long ingredientId, int limit) {
        Ingredient base = requireIngredient(ingredientId);
        Set<String> allergens = userAllergenMapper.selectList(Wrappers.<UserAllergen>lambdaQuery()
                        .eq(UserAllergen::getUserId, userId)).stream()
                .map(UserAllergen::getAllergenName).collect(Collectors.toSet());

        List<RecipeIngredient> sourceRows = recipeIngredientMapper.selectList(Wrappers.<RecipeIngredient>lambdaQuery()
                .eq(RecipeIngredient::getIngredientId, ingredientId));
        Set<Long> recipeIds = sourceRows.stream().map(RecipeIngredient::getRecipeId).collect(Collectors.toSet());
        if (recipeIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> counts = new HashMap<>();
        for (Long recipeId : recipeIds) {
            List<RecipeIngredient> rows = recipeIngredientMapper.selectList(Wrappers.<RecipeIngredient>lambdaQuery()
                    .eq(RecipeIngredient::getRecipeId, recipeId));
            for (RecipeIngredient row : rows) {
                if (!row.getIngredientId().equals(ingredientId)) {
                    counts.merge(row.getIngredientId(), 1, Integer::sum);
                }
            }
        }

        return counts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(Math.max(limit, 1))
                .map(entry -> {
                    Ingredient other = ingredientMapper.selectById(entry.getKey());
                    if (other == null || UserStatus.DISABLED.name().equals(other.getStatus())) {
                        return null;
                    }
                    if (allergens.stream().anyMatch(a -> other.getName().contains(a))) {
                        return null;
                    }
                    return IngredientPairingVO.builder()
                            .ingredientId(other.getId())
                            .name(other.getName())
                            .category(other.getCategory())
                            .coOccurrenceCount(entry.getValue())
                            .recommendReason("与" + base.getName() + "在 " + entry.getValue() + " 道菜谱中共现，营养互补")
                            .calorie(other.getCalorie())
                            .protein(other.getProtein())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Ingredient requireIngredient(Long id) {
        Ingredient ingredient = ingredientMapper.selectById(id);
        if (ingredient == null) {
            throw BizException.notFound("食材不存在");
        }
        return ingredient;
    }

    private Ingredient fromRequest(IngredientSaveRequest request) {
        return Ingredient.builder()
                .name(request.getName())
                .category(request.getCategory())
                .unit(request.getUnit() == null ? "g" : request.getUnit())
                .calorie(request.getCalorie())
                .protein(request.getProtein())
                .fat(request.getFat())
                .carbohydrate(request.getCarbohydrate())
                .sodium(request.getSodium())
                .vitaminC(request.getVitaminC())
                .vitaminA(request.getVitaminA())
                .build();
    }

    private void applyRequest(Ingredient ingredient, IngredientSaveRequest request) {
        ingredient.setName(request.getName());
        ingredient.setCategory(request.getCategory());
        if (request.getUnit() != null) {
            ingredient.setUnit(request.getUnit());
        }
        ingredient.setCalorie(request.getCalorie());
        ingredient.setProtein(request.getProtein());
        ingredient.setFat(request.getFat());
        ingredient.setCarbohydrate(request.getCarbohydrate());
        ingredient.setSodium(request.getSodium());
        ingredient.setVitaminC(request.getVitaminC());
        ingredient.setVitaminA(request.getVitaminA());
        if (request.getStatus() != null) {
            ingredient.setStatus(request.getStatus());
        }
    }

    private IngredientVO toVo(Ingredient ingredient) {
        return IngredientVO.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .category(ingredient.getCategory())
                .unit(ingredient.getUnit())
                .calorie(ingredient.getCalorie())
                .protein(ingredient.getProtein())
                .fat(ingredient.getFat())
                .carbohydrate(ingredient.getCarbohydrate())
                .sodium(ingredient.getSodium())
                .vitaminC(ingredient.getVitaminC())
                .vitaminA(ingredient.getVitaminA())
                .status(ingredient.getStatus())
                .build();
    }
}
