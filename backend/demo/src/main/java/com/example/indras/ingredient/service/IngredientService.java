package com.example.indras.ingredient.service;

import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.ingredient.dto.IngredientSaveRequest;
import com.example.indras.ingredient.vo.IngredientVO;

public interface IngredientService {

    PageResult<IngredientVO> page(PageQuery query, String category);

    IngredientVO getById(Long id);

    IngredientVO create(IngredientSaveRequest request);

    IngredientVO update(Long id, IngredientSaveRequest request);

    IngredientVO disable(Long id);
}
