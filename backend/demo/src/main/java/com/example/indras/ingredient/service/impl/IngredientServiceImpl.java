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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientMapper ingredientMapper;

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
                .status(ingredient.getStatus())
                .build();
    }
}
