package com.example.indras.healthgoal.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.enums.GoalCycleStatus;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.HealthCalculator;
import com.example.indras.common.util.PageUtils;
import com.example.indras.healthgoal.dto.HealthGoalCycleCreateRequest;
import com.example.indras.healthgoal.dto.HealthGoalCycleUpdateRequest;
import com.example.indras.healthgoal.entity.HealthGoalCycle;
import com.example.indras.healthgoal.mapper.HealthGoalCycleMapper;
import com.example.indras.healthgoal.service.HealthGoalCycleService;
import com.example.indras.healthgoal.vo.HealthGoalCompleteVO;
import com.example.indras.healthgoal.vo.HealthGoalCycleVO;
import com.example.indras.healthgoal.vo.HealthGoalProgressVO;
import com.example.indras.healthgoal.vo.WeightTrendPointVO;
import com.example.indras.health.entity.WeightRecord;
import com.example.indras.health.mapper.WeightRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthGoalCycleServiceImpl implements HealthGoalCycleService {

    private final HealthGoalCycleMapper healthGoalCycleMapper;
    private final WeightRecordMapper weightRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthGoalCycleVO create(Long userId, HealthGoalCycleCreateRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw BizException.businessRule("结束日期不能早于开始日期");
        }
        healthGoalCycleMapper.update(null, Wrappers.<HealthGoalCycle>lambdaUpdate()
                .eq(HealthGoalCycle::getUserId, userId)
                .eq(HealthGoalCycle::getStatus, GoalCycleStatus.ACTIVE.name())
                .set(HealthGoalCycle::getStatus, GoalCycleStatus.EXPIRED.name()));

        BigDecimal weeklyDelta = HealthCalculator.weeklyTargetDelta(
                request.getStartWeightKg(), request.getTargetWeightKg(), request.getStartDate(), request.getEndDate());
        HealthGoalCycle cycle = HealthGoalCycle.builder()
                .userId(userId)
                .goalType(request.getGoalType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startWeightKg(request.getStartWeightKg())
                .targetWeightKg(request.getTargetWeightKg())
                .targetCalorie(request.getTargetCalorie())
                .weeklyTargetDeltaKg(weeklyDelta)
                .progressPercent(BigDecimal.ZERO)
                .status(GoalCycleStatus.ACTIVE.name())
                .createdAt(LocalDateTime.now())
                .build();
        healthGoalCycleMapper.insert(cycle);
        return toVo(cycle);
    }

    @Override
    public HealthGoalCycleVO getCurrent(Long userId) {
        HealthGoalCycle cycle = healthGoalCycleMapper.selectOne(Wrappers.<HealthGoalCycle>lambdaQuery()
                .eq(HealthGoalCycle::getUserId, userId)
                .eq(HealthGoalCycle::getStatus, GoalCycleStatus.ACTIVE.name())
                .orderByDesc(HealthGoalCycle::getId)
                .last("LIMIT 1"));
        if (cycle == null) {
            throw BizException.notFound("当前没有进行中的目标周期");
        }
        return toVo(cycle);
    }

    @Override
    public PageResult<HealthGoalCycleVO> page(Long userId, PageQuery query) {
        Page<HealthGoalCycle> page = healthGoalCycleMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<HealthGoalCycle>lambdaQuery()
                        .eq(HealthGoalCycle::getUserId, userId)
                        .orderByDesc(HealthGoalCycle::getId));
        return PageUtils.toPageResult(page, this::toVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthGoalCycleVO update(Long userId, Long id, HealthGoalCycleUpdateRequest request) {
        HealthGoalCycle cycle = requireOwned(userId, id);
        cycle.setGoalType(request.getGoalType());
        cycle.setStartDate(request.getStartDate());
        cycle.setEndDate(request.getEndDate());
        cycle.setStartWeightKg(request.getStartWeightKg());
        cycle.setTargetWeightKg(request.getTargetWeightKg());
        cycle.setTargetCalorie(request.getTargetCalorie());
        cycle.setWeeklyTargetDeltaKg(HealthCalculator.weeklyTargetDelta(
                request.getStartWeightKg(), request.getTargetWeightKg(), request.getStartDate(), request.getEndDate()));
        healthGoalCycleMapper.updateById(cycle);
        return toVo(cycle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthGoalCompleteVO complete(Long userId, Long id) {
        HealthGoalCycle cycle = requireOwned(userId, id);
        cycle.setStatus(GoalCycleStatus.COMPLETED.name());
        cycle.setProgressPercent(BigDecimal.valueOf(100));
        healthGoalCycleMapper.updateById(cycle);
        return HealthGoalCompleteVO.builder()
                .id(id)
                .status(GoalCycleStatus.COMPLETED.name())
                .summary("本周期完成度 " + cycle.getProgressPercent() + "%，体重趋势基本符合预期。")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthGoalCycleVO cancel(Long userId, Long id) {
        HealthGoalCycle cycle = requireOwned(userId, id);
        cycle.setStatus(GoalCycleStatus.CANCELLED.name());
        healthGoalCycleMapper.updateById(cycle);
        return toVo(cycle);
    }

    @Override
    public HealthGoalProgressVO progress(Long userId, Long id) {
        HealthGoalCycle cycle = requireOwned(userId, id);
        List<WeightRecord> records = weightRecordMapper.selectList(Wrappers.<WeightRecord>lambdaQuery()
                .eq(WeightRecord::getUserId, userId)
                .ge(WeightRecord::getRecordDate, cycle.getStartDate())
                .orderByAsc(WeightRecord::getRecordDate));
        BigDecimal currentWeight = records.isEmpty() ? cycle.getStartWeightKg() : records.get(records.size() - 1).getWeightKg();
        long days = java.time.temporal.ChronoUnit.DAYS.between(cycle.getStartDate(), java.time.LocalDate.now());
        long totalDays = Math.max(java.time.temporal.ChronoUnit.DAYS.between(cycle.getStartDate(), cycle.getEndDate()), 1);
        BigDecimal expected = cycle.getStartWeightKg().add(
                cycle.getWeeklyTargetDeltaKg()
                        .multiply(BigDecimal.valueOf(days / 7.0))
                        .setScale(2, RoundingMode.HALF_UP));
        BigDecimal progress = cycle.getTargetWeightKg().subtract(cycle.getStartWeightKg()).abs().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : cycle.getStartWeightKg().subtract(currentWeight)
                .divide(cycle.getStartWeightKg().subtract(cycle.getTargetWeightKg()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        return HealthGoalProgressVO.builder()
                .cycleId(id)
                .progressPercent(progress)
                .currentWeightKg(currentWeight)
                .expectedWeightKg(expected)
                .deviationKg(currentWeight.subtract(expected))
                .warning(currentWeight.subtract(expected).abs().compareTo(BigDecimal.ONE) > 0)
                .trend(records.stream().map(r -> WeightTrendPointVO.builder()
                        .date(r.getRecordDate()).weightKg(r.getWeightKg()).build()).toList())
                .build();
    }

    private HealthGoalCycle requireOwned(Long userId, Long id) {
        HealthGoalCycle cycle = healthGoalCycleMapper.selectById(id);
        if (cycle == null || !cycle.getUserId().equals(userId)) {
            throw BizException.notFound("目标周期不存在");
        }
        return cycle;
    }

    private HealthGoalCycleVO toVo(HealthGoalCycle cycle) {
        return HealthGoalCycleVO.builder()
                .id(cycle.getId())
                .goalType(cycle.getGoalType())
                .startDate(cycle.getStartDate())
                .endDate(cycle.getEndDate())
                .startWeightKg(cycle.getStartWeightKg())
                .targetWeightKg(cycle.getTargetWeightKg())
                .targetCalorie(cycle.getTargetCalorie())
                .weeklyTargetDeltaKg(cycle.getWeeklyTargetDeltaKg())
                .progressPercent(cycle.getProgressPercent())
                .status(cycle.getStatus())
                .build();
    }
}
