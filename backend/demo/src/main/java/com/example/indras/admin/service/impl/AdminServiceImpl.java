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
import com.example.indras.risk.entity.NutritionRiskRule;
import com.example.indras.risk.mapper.NutritionRiskResultMapper;
import com.example.indras.risk.mapper.NutritionRiskRuleMapper;
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
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

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
    private final NutritionRiskRuleMapper nutritionRiskRuleMapper;
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
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long id) {
        UserContext.requireAdmin();
        CommunityPost post = communityPostMapper.selectById(id);
        if (post == null) {
            throw BizException.notFound("帖子不存在");
        }
        post.setStatus("DELETED");
        communityPostMapper.updateById(post);
    }

    @Override
    public PageResult<AiCallLogVO> pageAiCallLogs(PageQuery query) {
        UserContext.requireAdmin();
        Page<AiCallLog> page = aiCallLogMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<AiCallLog>lambdaQuery().orderByDesc(AiCallLog::getId));
        return PageUtils.toPageResult(page, log -> AiCallLogVO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .callerName(resolveUsername(log.getUserId()))
                .scene(log.getScene())
                .model(resolveAiModel(log.getScene()))
                .endpoint(resolveAiEndpoint(log.getScene()))
                .requestSummary(log.getRequestSummary())
                .elapsedMs(log.getElapsedMs())
                .latencyMs(log.getElapsedMs())
                .inputTokens(estimateInputTokens(log.getRequestSummary()))
                .outputTokens(estimateOutputTokens(log))
                .success(log.getSuccess())
                .status(resolveAiStatus(log))
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
        Long total = nutritionReportMapper.selectCount(Wrappers.<NutritionReport>lambdaQuery());
        Long activeUsers = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStatus, UserStatus.ENABLED.name()));
        List<NutritionReport> reports = nutritionReportMapper.selectList(Wrappers.<NutritionReport>lambdaQuery()
                .isNotNull(NutritionReport::getCompletionRate));
        BigDecimal avgCompletion = reports.isEmpty() ? BigDecimal.ZERO
                : reports.stream()
                .map(NutritionReport::getCompletionRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(reports.size()), 2, RoundingMode.HALF_UP);
        List<NutritionReport> allReports = nutritionReportMapper.selectList(Wrappers.<NutritionReport>lambdaQuery()
                .orderByAsc(NutritionReport::getCreatedAt));
        long reportUserCount = allReports.stream()
                .map(NutritionReport::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        List<NutritionRiskResult> riskResults = nutritionRiskResultMapper.selectList(Wrappers.<NutritionRiskResult>lambdaQuery());
        Map<Long, String> ruleNameMap = nutritionRiskRuleMapper.selectList(Wrappers.<NutritionRiskRule>lambdaQuery()).stream()
                .collect(Collectors.toMap(NutritionRiskRule::getId, NutritionRiskRule::getRuleName, (a, b) -> a));
        List<NutritionReportStatisticsVO.TopRiskVO> topRisks = riskResults.stream()
                .filter(result -> result.getRuleId() != null)
                .collect(Collectors.groupingBy(NutritionRiskResult::getRuleId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> NutritionReportStatisticsVO.TopRiskVO.builder()
                        .ruleId(entry.getKey())
                        .ruleName(ruleNameMap.getOrDefault(entry.getKey(), "规则 #" + entry.getKey()))
                        .count(entry.getValue())
                        .build())
                .toList();
        List<NutritionReportStatisticsVO.DailyReportTrendVO> trend = allReports.stream()
                .filter(report -> report.getCreatedAt() != null)
                .collect(Collectors.groupingBy(report -> report.getCreatedAt().toLocalDate().toString(),
                        java.util.TreeMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> NutritionReportStatisticsVO.DailyReportTrendVO.builder()
                        .date(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
        BigDecimal userReportRate = activeUsers == null || activeUsers == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(reportUserCount)
                .divide(BigDecimal.valueOf(activeUsers), 4, RoundingMode.HALF_UP);
        BigDecimal riskTriggerRate = total == null || total == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(riskResults.size())
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
        return NutritionReportStatisticsVO.builder()
                .totalReports(total == null ? 0 : total.intValue())
                .weeklyCount(weekly == null ? 0 : weekly.intValue())
                .monthlyCount(monthly == null ? 0 : monthly.intValue())
                .averageScore(avgCompletion)
                .userReportRate(userReportRate)
                .riskTriggerRate(riskTriggerRate)
                .topRisks(topRisks)
                .dailyReportTrend(trend)
                .weeklyReportCount(weekly == null ? 0 : weekly.intValue())
                .monthlyReportCount(monthly == null ? 0 : monthly.intValue())
                .activeUserCount(activeUsers == null ? 0 : activeUsers.intValue())
                .avgCompletionRate(avgCompletion)
                .build();
    }

    private String resolveUsername(Long userId) {
        if (userId == null) {
            return "system";
        }
        SysUser user = sysUserMapper.selectById(userId);
        return user == null ? "user-" + userId : user.getUsername();
    }

    private String resolveAiModel(String scene) {
        if (scene == null) {
            return "unknown";
        }
        if (scene.contains("IMAGE")) {
            return "kimi-vision";
        }
        if (scene.contains("ADVISOR") || scene.contains("CHAT")) {
            return "deepseek-chat";
        }
        return "deepseek-chat";
    }

    private String resolveAiEndpoint(String scene) {
        if (scene == null) {
            return "/ai";
        }
        return switch (scene) {
            case "TEXT_PARSE" -> "/ai-recipes/parse";
            case "IMAGE_PARSE" -> "/ai-recipes/parse-image";
            case "ADVISOR_CHAT" -> "/ai/advisor/chat";
            case "MEAL_PLAN_GENERATE" -> "/meal-plans/generate/day";
            case "RISK_EVALUATE" -> "/nutrition-risk-rules/evaluate";
            default -> "/ai/" + scene.toLowerCase().replace('_', '-');
        };
    }

    private Integer estimateInputTokens(String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }
        return Math.max(1, text.length() / 2);
    }

    private Integer estimateOutputTokens(AiCallLog log) {
        if (!Boolean.TRUE.equals(log.getSuccess())) {
            return 0;
        }
        return Math.max(24, (log.getElapsedMs() == null ? 1000 : log.getElapsedMs()) / 40);
    }

    private String resolveAiStatus(AiCallLog log) {
        if (Boolean.TRUE.equals(log.getSuccess())) {
            return "SUCCESS";
        }
        String error = log.getErrorMessage();
        if ((log.getElapsedMs() != null && log.getElapsedMs() >= 120000)
                || (error != null && error.toLowerCase().contains("timeout"))
                || (error != null && error.contains("超时"))) {
            return "TIMEOUT";
        }
        return "ERROR";
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
