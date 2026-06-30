package com.example.indras.auth.service;

import com.example.indras.auth.dto.ChangePasswordRequest;
import com.example.indras.auth.dto.LoginRequest;
import com.example.indras.auth.dto.RegisterRequest;
import com.example.indras.auth.vo.LoginResponseVO;
import com.example.indras.auth.vo.RegisterResponseVO;
import com.example.indras.auth.vo.UserVO;

public interface AuthService {

    RegisterResponseVO register(RegisterRequest request);

    LoginResponseVO login(LoginRequest request);

    UserVO currentUser(Long userId);

    boolean changePassword(Long userId, ChangePasswordRequest request);
}
