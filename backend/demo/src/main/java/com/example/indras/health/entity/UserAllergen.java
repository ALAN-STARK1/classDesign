package com.example.indras.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@TableName("user_allergen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAllergen {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("allergen_name")
    private String allergenName;
}
