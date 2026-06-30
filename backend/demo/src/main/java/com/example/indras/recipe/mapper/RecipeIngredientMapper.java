package com.example.indras.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.indras.recipe.entity.RecipeIngredient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeIngredientMapper extends BaseMapper<RecipeIngredient> {
}
