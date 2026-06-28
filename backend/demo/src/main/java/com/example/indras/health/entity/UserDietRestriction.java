package com.example.indras.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@TableName("user_diet_restriction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDietRestriction {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("restriction_name")
    private String restrictionName;
}
