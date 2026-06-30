package com.example.indras.auth.controller;

import com.example.indras.auth.dto.ChangePasswordRequest;
import com.example.indras.auth.dto.LoginRequest;
import com.example.indras.auth.dto.RegisterRequest;
import com.example.indras.auth.service.AuthService;
import com.example.indras.auth.vo.LoginResponseVO;
import com.example.indras.auth.vo.RegisterResponseVO;
import com.example.indras.auth.vo.UserVO;
import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.context.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponseVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(authService.currentUser(UserContext.requireUserId()));
    }

    @PutMapping("/password")
    public ApiResponse<Boolean> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ApiResponse.success(authService.changePassword(UserContext.requireUserId(), request));
    }
}
