package com.example.indras.recipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@TableName("recipe_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStep {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("recipe_id")
    private Long recipeId;
    @TableField("step_no")
    private Integer stepNo;
    private String content;
}
