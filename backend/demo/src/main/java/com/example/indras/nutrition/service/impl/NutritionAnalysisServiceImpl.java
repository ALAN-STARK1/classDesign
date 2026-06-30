package com.example.indras.nutrition.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.indras.common.vo.MacroRatioVO;
import com.example.indras.health.entity.HealthProfile;
import com.example.indras.health.mapper.HealthProfileMapper;
import com.example.indras.mealrecord.entity.MealRecord;
import com.example.indras.mealrecord.mapper.MealRecordMapper;
import com.example.indras.mealrecord.service.MealRecordService;
import com.example.indras.mealrecord.vo.DailyMealRecordVO;
import com.example.indras.nutrition.service.NutritionAnalysisService;
import com.example.indras.nutrition.vo.DailyNutritionPointVO;
import com.example.indras.nutrition.vo.NutritionGapVO;
import com.example.indras.nutrition.vo.NutritionRangeVO;
import com.example.indras.nutrition.vo.TodayNutritionVO;
import com.example.indras.risk.entity.NutritionRiskResult;
import com.example.indras.risk.mapper.NutritionRiskResultMapper;
import com.example.indras.risk.vo.NutritionRiskResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionAnalysisServiceImpl implements NutritionAnalysisService {

    private final MealRecordMapper mealRecordMapper;
    private final MealRecordService mealRecordService;
    private final HealthProfileMapper healthProfileMapper;
    private final NutritionRiskResultMapper nutritionRiskResultMapper;

    @Override
    public TodayNutritionVO today(Long userId, LocalDate date) {
        LocalDate target = date == null ? LocalDate.now() : date;
        return buildToday(userId, target);
    }

    @Override
    public NutritionRangeVO range(Long userId, LocalDate startDate, LocalDate endDate) {
        List<DailyNutritionPointVO> days = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyMealRecordVO daily = mealRecordService.getDay(userId, date);
            if (daily.getSummary() == null) {
                continue;
            }
            days.add(DailyNutritionPointVO.builder()
                    .date(date)
                    .calorie(daily.getSummary().getCalorie())
                    .protein(daily.getSummary().getProtein())
                    .fat(daily.getSummary().getFat())
                    .carbohydrate(daily.getSummary().getCarbohydrate())
                    .build());
        }
        return NutritionRangeVO.builder().startDate(startDate).endDate(endDate).days(days).build();
    }

    @Override
    public NutritionGapVO gap(Long userId, LocalDate date) {
        TodayNutritionVO today = buildToday(userId, date);
        int target = today.getTargetCalorie() == null ? 0 : today.getTargetCalorie();
        int actual = today.getTotalCalorie() == null ? 0 : today.getTotalCalorie().intValue();
        return NutritionGapVO.builder()
                .date(date)
                .calorieGap(target - actual)
                .proteinGap(0)
                .fatGap(0)
                .carbohydrateGap(0)
                .suggestions(actual > target ? List.of("今日热量已超出目标，建议减少晚餐油脂和主食。") : List.of("可适量补充优质蛋白。"))
                .build();
    }

    @Override
    public List<NutritionRiskResultVO> risks(Long userId, LocalDate startDate, LocalDate endDate) {
        return nutritionRiskResultMapper.selectList(Wrappers.<NutritionRiskResult>lambdaQuery()
                        .eq(NutritionRiskResult::getUserId, userId)
                        .ge(NutritionRiskResult::getRiskDate, startDate)
                        .le(NutritionRiskResult::getRiskDate, endDate))
                .stream()
                .map(r -> NutritionRiskResultVO.builder()
                        .id(r.getId())
                        .riskDate(r.getRiskDate())
                        .severity(r.getSeverity())
                        .sourceType(r.getSourceType())
                        .sourceId(r.getSourceId())
                        .message(r.getMessage())
                        .suggestion(r.getSuggestion())
                        .build())
                .toList();
    }

    private TodayNutritionVO buildToday(Long userId, LocalDate date) {
        List<MealRecord> records = mealRecordMapper.selectList(Wrappers.<MealRecord>lambdaQuery()
                .eq(MealRecord::getUserId, userId)
                .eq(MealRecord::getRecordDate, date));
        BigDecimal calorie = BigDecimal.ZERO;
        BigDecimal protein = BigDecimal.ZERO;
        BigDecimal fat = BigDecimal.ZERO;
        BigDecimal carb = BigDecimal.ZERO;
        for (MealRecord record : records) {
            calorie = calorie.add(nullToZero(record.getTotalCalorie()));
            protein = protein.add(nullToZero(record.getTotalProtein()));
            fat = fat.add(nullToZero(record.getTotalFat()));
            carb = carb.add(nullToZero(record.getTotalCarbohydrate()));
        }
        HealthProfile profile = healthProfileMapper.selectOne(Wrappers.<HealthProfile>lambdaQuery()
                .eq(HealthProfile::getUserId, userId));
        int target = profile == null || profile.getDailyCalorieTarget() == null ? 1600 : profile.getDailyCalorieTarget();
        int percent = target == 0 ? 0 : calorie.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(target), 0, RoundingMode.HALF_UP).intValue();
        BigDecimal macroTotal = protein.add(fat).add(carb);
        MacroRatioVO ratio = macroTotal.compareTo(BigDecimal.ZERO) == 0 ? new MacroRatioVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
                : new MacroRatioVO(
                protein.multiply(BigDecimal.valueOf(100)).divide(macroTotal, 1, RoundingMode.HALF_UP),
                fat.multiply(BigDecimal.valueOf(100)).divide(macroTotal, 1, RoundingMode.HALF_UP),
                carb.multiply(BigDecimal.valueOf(100)).divide(macroTotal, 1, RoundingMode.HALF_UP));
        return TodayNutritionVO.builder()
                .date(date)
                .targetCalorie(target)
                .totalCalorie(calorie)
                .caloriePercent(percent)
                .protein(protein)
                .fat(fat)
                .carbohydrate(carb)
                .macroRatio(ratio)
                .build();
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
