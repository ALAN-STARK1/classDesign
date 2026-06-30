package com.example.indras.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.indras.auth.dto.ChangePasswordRequest;
import com.example.indras.auth.dto.LoginRequest;
import com.example.indras.auth.dto.RegisterRequest;
import com.example.indras.auth.service.AuthService;
import com.example.indras.auth.vo.LoginResponseVO;
import com.example.indras.auth.vo.RegisterResponseVO;
import com.example.indras.auth.vo.UserVO;
import com.example.indras.common.enums.Role;
import com.example.indras.common.enums.UserStatus;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.JwtUtil;
import com.example.indras.health.entity.UserPreference;
import com.example.indras.health.mapper.UserPreferenceMapper;
import com.example.indras.user.entity.SysUser;
import com.example.indras.user.entity.UserProfile;
import com.example.indras.user.mapper.SysUserMapper;
import com.example.indras.user.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final UserProfileMapper userProfileMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire-seconds:7200}")
    private long jwtExpireSeconds;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponseVO register(RegisterRequest request) {
        Long usernameCount = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, request.getUsername()));
        if (usernameCount != null && usernameCount > 0) {
            throw BizException.conflict("用户名已存在");
        }
        Long emailCount = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getEmail, request.getEmail()));
        if (emailCount != null && emailCount > 0) {
            throw BizException.conflict("邮箱已存在");
        }

        SysUser user = SysUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER.name())
                .status(UserStatus.ENABLED.name())
                .createdAt(LocalDateTime.now())
                .build();
        sysUserMapper.insert(user);

        userProfileMapper.insert(UserProfile.builder()
                .userId(user.getId())
                .nickname(request.getUsername())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        userPreferenceMapper.insert(UserPreference.builder()
                .userId(user.getId())
                .likedTags("[]")
                .dislikedIngredients("[]")
                .preferredCuisines("[]")
                .spiceLevel("MEDIUM")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        return RegisterResponseVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    @Override
    public LoginResponseVO login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, request.getUsername()));
        if (user == null) {
            throw BizException.unauthorized("用户名或密码错误");
        }
        if (!UserStatus.ENABLED.name().equals(user.getStatus())) {
            throw BizException.forbidden("账号已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw BizException.unauthorized("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), jwtSecret, jwtExpireSeconds);
        return LoginResponseVO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn((int) jwtExpireSeconds)
                .user(UserVO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
    }

    @Override
    public UserVO currentUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw BizException.notFound("用户不存在");
        }
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw BizException.notFound("用户不存在");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw BizException.badRequest("原密码不正确");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return sysUserMapper.updateById(user) > 0;
    }
}
