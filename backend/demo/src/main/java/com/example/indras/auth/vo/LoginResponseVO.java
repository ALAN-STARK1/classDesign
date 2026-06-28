package com.example.indras.auth.vo;

import lombok.*;
import com.example.indras.auth.vo.UserVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO {

    private String token;
    private String tokenType;
    private Integer expiresIn;
    private UserVO user;
}
