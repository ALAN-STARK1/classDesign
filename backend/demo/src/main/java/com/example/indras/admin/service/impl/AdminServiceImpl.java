package com.example.indras.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.admin.dto.ReviewRequest;
import com.example.indras.admin.service.AdminService;
import com.example.indras.admin.vo.AiCallLogVO;
import com.example.indras.admin.vo.DemoDataResetVO;
import com.example.indras.admin.vo.NutritionReportStatisticsVO;
import com.example.indras.admin.vo.UserAdminVO;
import com.example.indras.airecipe.entity.AiCallLog;
import com.example.indras.airecipe.mapper.AiCallLogMapper;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.common.enums.RecipeStatus;
import com.example.indras.common.enums.UserStatus;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.PageUtils;
import com.example.indras.community.entity.CommunityPost;
import com.example.indras.community.mapper.CommunityPostMapper;
import com.example.indras.community.service.CommunityService;
import com.example.indras.community.vo.CommunityPostDetailVO;
import com.example.indras.recipe.entity.Recipe;
import com.example.indras.recipe.mapper.RecipeMapper;
import com.example.indras.recipe.service.RecipeService;
import com.example.indras.recipe.vo.RecipeDetailVO;
import com.example.indras.report.entity.NutritionReport;
import com.example.indras.report.mapper.NutritionReportMapper;
import com.example.indras.risk.entity.NutritionRiskResult;
import com.example.indras.risk.mapper.NutritionRiskResultMapper;
import com.example.indras.risk.vo.NutritionRiskResultVO;
import com.example.indras.user.entity.SysUser;
import com.example.indras.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysUserMapper sysUserMapper;
    private final RecipeMapper recipeMapper;
    private final RecipeService recipeService;
    private final CommunityPostMapper communityPostMapper;
    private final CommunityService communityService;
    private final AiCallLogMapper aiCallLogMapper;
    private final NutritionRiskResultMapper nutritionRiskResultMapper;
    private final NutritionReportMapper nutritionReportMapper;

    @Override
    public PageResult<UserAdminVO> pageUsers(PageQuery query) {
        UserContext.requireAdmin();
        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<SysUser>lambdaQuery()
                        .like(StringUtils.hasText(query.getKeyword()), SysUser::getUsername, query.getKeyword())
                        .orderByDesc(SysUser::getId));
        return PageUtils.toPageResult(page, this::toUserVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAdminVO disableUser(Long id) {
        UserContext.requireAdmin();
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw BizException.notFound("用户不存在");
        }
        user.setStatus(UserStatus.DISABLED.name());
        sysUserMapper.updateById(user);
        return toUserVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecipeDetailVO reviewRecipe(Long id, ReviewRequest request) {
        UserContext.requireAdmin();
        Recipe recipe = recipeMapper.selectById(id);
        if (recipe == null) {
            throw BizException.notFound("菜谱不存在");
        }
        recipe.setStatus(Boolean.TRUE.equals(request.getApproved()) ? RecipeStatus.ONLINE.name() : RecipeStatus.OFFLINE.name());
        recipeMapper.updateById(recipe);
        return recipeService.detail(UserContext.requireUserId(), id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityPostDetailVO reviewPost(Long id, ReviewRequest request) {
        UserContext.requireAdmin();
        CommunityPost post = communityPostMapper.selectById(id);
        if (post == null) {
            throw BizException.notFound("帖子不存在");
        }
        if (Boolean.TRUE.equals(request.getApproved())) {
            post.setStatus("ONLINE");
            post.setPublishedAt(LocalDateTime.now());
        } else {
            post.setStatus("REJECTED");
        }
        communityPostMapper.updateById(post);
        return communityService.postDetail(UserContext.requireUserId(), id);
    }

    @Override
    public PageResult<AiCallLogVO> pageAiCallLogs(PageQuery query) {
        UserContext.requireAdmin();
        Page<AiCallLog> page = aiCallLogMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<AiCallLog>lambdaQuery().orderByDesc(AiCallLog::getId));
        return PageUtils.toPageResult(page, log -> AiCallLogVO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .scene(log.getScene())
                .requestSummary(log.getRequestSummary())
                .elapsedMs(log.getElapsedMs())
                .success(log.getSuccess())
                .errorMessage(log.getErrorMessage())
                .createdAt(log.getCreatedAt() == null ? null
                        : OffsetDateTime.of(log.getCreatedAt(), ZoneOffset.ofHours(8)))
                .build());
    }

    @Override
    public PageResult<NutritionRiskResultVO> pageRiskResults(PageQuery query, String severity) {
        UserContext.requireAdmin();
        Page<NutritionRiskResult> page = nutritionRiskResultMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<NutritionRiskResult>lambdaQuery()
                        .eq(StringUtils.hasText(severity), NutritionRiskResult::getSeverity, severity)
                        .orderByDesc(NutritionRiskResult::getId));
        return PageUtils.toPageResult(page, result -> NutritionRiskResultVO.builder()
                .id(result.getId())
                .riskDate(result.getRiskDate())
                .severity(result.getSeverity())
                .sourceType(result.getSourceType())
                .sourceId(result.getSourceId())
                .message(result.getMessage())
                .suggestion(result.getSuggestion())
                .build());
    }

    @Override
    public NutritionReportStatisticsVO reportStatistics() {
        UserContext.requireAdmin();
        Long weekly = nutritionReportMapper.selectCount(Wrappers.<NutritionReport>lambdaQuery()
                .eq(NutritionReport::getReportType, "WEEKLY"));
        Long monthly = nutritionReportMapper.selectCount(Wrappers.<NutritionReport>lambdaQuery()
                .eq(NutritionReport::getReportType, "MONTHLY"));
        Long activeUsers = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStatus, UserStatus.ENABLED.name()));
        List<NutritionReport> reports = nutritionReportMapper.selectList(Wrappers.<NutritionReport>lambdaQuery()
                .isNotNull(NutritionReport::getCompletionRate));
        BigDecimal avgCompletion = reports.isEmpty() ? BigDecimal.ZERO
                : reports.stream()
                .map(NutritionReport::getCompletionRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(reports.size()), 2, RoundingMode.HALF_UP);
        return NutritionReportStatisticsVO.builder()
                .weeklyReportCount(weekly == null ? 0 : weekly.intValue())
                .monthlyReportCount(monthly == null ? 0 : monthly.intValue())
                .activeUserCount(activeUsers == null ? 0 : activeUsers.intValue())
                .avgCompletionRate(avgCompletion)
                .build();
    }

    @Override
    public DemoDataResetVO resetDemoData() {
        UserContext.requireAdmin();
        return DemoDataResetVO.builder()
                .success(true)
                .message("演示数据重置需手动执行 schema.sql 与 data.sql")
                .build();
    }

    private UserAdminVO toUserVo(SysUser user) {
        return UserAdminVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt() == null ? null
                        : OffsetDateTime.of(user.getCreatedAt(), ZoneOffset.ofHours(8)))
                .build();
    }
}
