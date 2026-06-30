package com.example.indras.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.indras.recipe.entity.RecipeFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeFavoriteMapper extends BaseMapper<RecipeFavorite> {
}
