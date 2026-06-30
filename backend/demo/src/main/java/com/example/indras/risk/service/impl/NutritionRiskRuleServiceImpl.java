package com.example.indras.risk.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.enums.UserStatus;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.PageUtils;
import com.example.indras.health.entity.HealthProfile;
import com.example.indras.health.mapper.HealthProfileMapper;
import com.example.indras.mealrecord.entity.MealRecord;
import com.example.indras.mealrecord.mapper.MealRecordMapper;
import com.example.indras.risk.dto.NutritionRiskRuleSaveRequest;
import com.example.indras.risk.dto.RiskEvaluateRequest;
import com.example.indras.risk.dto.RiskRuleStatusRequest;
import com.example.indras.risk.entity.NutritionRiskResult;
import com.example.indras.risk.entity.NutritionRiskRule;
import com.example.indras.risk.mapper.NutritionRiskResultMapper;
import com.example.indras.risk.mapper.NutritionRiskRuleMapper;
import com.example.indras.risk.service.NutritionRiskRuleService;
import com.example.indras.risk.vo.NutritionRiskResultVO;
import com.example.indras.risk.vo.NutritionRiskRuleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionRiskRuleServiceImpl implements NutritionRiskRuleService {

    private final NutritionRiskRuleMapper nutritionRiskRuleMapper;
    private final NutritionRiskResultMapper nutritionRiskResultMapper;
    private final MealRecordMapper mealRecordMapper;
    private final HealthProfileMapper healthProfileMapper;

    @Override
    public PageResult<NutritionRiskRuleVO> page(PageQuery query, String scenario) {
        Page<NutritionRiskRule> page = nutritionRiskRuleMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<NutritionRiskRule>lambdaQuery()
                        .eq(StringUtils.hasText(scenario), NutritionRiskRule::getScenario, scenario)
                        .orderByAsc(NutritionRiskRule::getId));
        return PageUtils.toPageResult(page, this::toVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NutritionRiskRuleVO create(NutritionRiskRuleSaveRequest request) {
        NutritionRiskRule rule = fromRequest(request);
        rule.setStatus(UserStatus.ENABLED.name());
        nutritionRiskRuleMapper.insert(rule);
        return toVo(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NutritionRiskRuleVO update(Long id, NutritionRiskRuleSaveRequest request) {
        NutritionRiskRule rule = requireRule(id);
        applyRequest(rule, request);
        nutritionRiskRuleMapper.updateById(rule);
        return toVo(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NutritionRiskRuleVO updateStatus(Long id, RiskRuleStatusRequest request) {
        NutritionRiskRule rule = requireRule(id);
        rule.setStatus(request.getStatus());
        nutritionRiskRuleMapper.updateById(rule);
        return toVo(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<NutritionRiskResultVO> evaluate(Long userId, RiskEvaluateRequest request) {
        LocalDate date = request.getDate() == null ? LocalDate.now() : request.getDate();
        HealthProfile profile = healthProfileMapper.selectOne(Wrappers.<HealthProfile>lambdaQuery()
                .eq(HealthProfile::getUserId, userId));
        String scenario = profile == null ? "MAINTAIN" : profile.getHealthGoal();
        List<NutritionRiskRule> rules = nutritionRiskRuleMapper.selectList(Wrappers.<NutritionRiskRule>lambdaQuery()
                .eq(NutritionRiskRule::getScenario, scenario)
                .eq(NutritionRiskRule::getStatus, UserStatus.ENABLED.name()));

        BigDecimal dayCalorie = mealRecordMapper.selectList(Wrappers.<MealRecord>lambdaQuery()
                        .eq(MealRecord::getUserId, userId)
                        .eq(MealRecord::getRecordDate, date))
                .stream()
                .map(MealRecord::getTotalCalorie)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b == null ? BigDecimal.ZERO : b));

        List<NutritionRiskResultVO> results = new ArrayList<>();
        for (NutritionRiskRule rule : rules) {
            if (!"CALORIE".equals(rule.getNutrient())) {
                continue;
            }
            boolean hit = matchRule(rule, dayCalorie, profile == null ? 1600 : profile.getDailyCalorieTarget());
            if (!hit) {
                continue;
            }
            NutritionRiskResult entity = NutritionRiskResult.builder()
                    .userId(userId)
                    .ruleId(rule.getId())
                    .sourceType(request.getSourceType())
                    .sourceId(request.getSourceId())
                    .riskDate(date)
                    .severity(rule.getSeverity())
                    .message(rule.getMessage())
                    .suggestion("请调整当日饮食结构")
                    .createdAt(LocalDateTime.now())
                    .build();
            nutritionRiskResultMapper.insert(entity);
            results.add(NutritionRiskResultVO.builder()
                    .id(entity.getId())
                    .riskDate(date)
                    .severity(entity.getSeverity())
                    .sourceType(entity.getSourceType())
                    .sourceId(entity.getSourceId())
                    .message(entity.getMessage())
                    .suggestion(entity.getSuggestion())
                    .build());
        }
        return results;
    }

    private boolean matchRule(NutritionRiskRule rule, BigDecimal actual, int target) {
        if ("GT".equals(rule.getOperator()) && rule.getThresholdMax() != null) {
            return actual.compareTo(rule.getThresholdMax()) > 0;
        }
        if ("GT".equals(rule.getOperator()) && rule.getThresholdMax() == null) {
            return actual.compareTo(BigDecimal.valueOf(target)) > 0;
        }
        return false;
    }

    private NutritionRiskRule requireRule(Long id) {
        NutritionRiskRule rule = nutritionRiskRuleMapper.selectById(id);
        if (rule == null) {
            throw BizException.notFound("风险规则不存在");
        }
        return rule;
    }

    private NutritionRiskRule fromRequest(NutritionRiskRuleSaveRequest request) {
        return NutritionRiskRule.builder()
                .ruleCode(request.getRuleCode())
                .ruleName(request.getRuleName())
                .scenario(request.getScenario())
                .nutrient(request.getNutrient())
                .operator(request.getOperator())
                .thresholdMin(request.getThresholdMin())
                .thresholdMax(request.getThresholdMax())
                .severity(request.getSeverity())
                .message(request.getMessage())
                .status(request.getStatus())
                .build();
    }

    private void applyRequest(NutritionRiskRule rule, NutritionRiskRuleSaveRequest request) {
        rule.setRuleCode(request.getRuleCode());
        rule.setRuleName(request.getRuleName());
        rule.setScenario(request.getScenario());
        rule.setNutrient(request.getNutrient());
        rule.setOperator(request.getOperator());
        rule.setThresholdMin(request.getThresholdMin());
        rule.setThresholdMax(request.getThresholdMax());
        rule.setSeverity(request.getSeverity());
        rule.setMessage(request.getMessage());
        if (request.getStatus() != null) {
            rule.setStatus(request.getStatus());
        }
    }

    private NutritionRiskRuleVO toVo(NutritionRiskRule rule) {
        return NutritionRiskRuleVO.builder()
                .id(rule.getId())
                .ruleCode(rule.getRuleCode())
                .ruleName(rule.getRuleName())
                .scenario(rule.getScenario())
                .nutrient(rule.getNutrient())
                .operator(rule.getOperator())
                .thresholdMin(rule.getThresholdMin())
                .thresholdMax(rule.getThresholdMax())
                .severity(rule.getSeverity())
                .message(rule.getMessage())
                .status(rule.getStatus())
                .build();
    }
}
