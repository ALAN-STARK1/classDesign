package com.example.indras.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("sys_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    @TableField("password_hash")
    private String passwordHash;
    private String role;
    private String status;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
