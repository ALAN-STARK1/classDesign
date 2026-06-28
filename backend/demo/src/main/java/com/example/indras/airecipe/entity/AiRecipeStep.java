package com.example.indras.airecipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@TableName("ai_recipe_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecipeStep {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("ai_recipe_id")
    private Long aiRecipeId;
    @TableField("step_no")
    private Integer stepNo;
    private String content;
}
