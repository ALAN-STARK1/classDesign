package com.example.indras.ingredient.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.ingredient.dto.IngredientSaveRequest;
import com.example.indras.ingredient.service.IngredientService;
import com.example.indras.ingredient.vo.IngredientVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ApiResponse<PageResult<IngredientVO>> page(PageQuery query,
                                                    @RequestParam(required = false) String category) {
        return ApiResponse.success(ingredientService.page(query, category));
    }

    @GetMapping("/{id}")
    public ApiResponse<IngredientVO> detail(@PathVariable Long id) {
        return ApiResponse.success(ingredientService.getById(id));
    }

    @PostMapping
    public ApiResponse<IngredientVO> create(@Valid @RequestBody IngredientSaveRequest request) {
        return ApiResponse.success(ingredientService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<IngredientVO> update(@PathVariable Long id,
                                            @Valid @RequestBody IngredientSaveRequest request) {
        return ApiResponse.success(ingredientService.update(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ApiResponse<IngredientVO> disable(@PathVariable Long id) {
        return ApiResponse.success(ingredientService.disable(id));
    }
}
