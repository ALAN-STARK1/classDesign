package com.example.indras.ingredient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.indras.ingredient.entity.Ingredient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IngredientMapper extends BaseMapper<Ingredient> {
}
