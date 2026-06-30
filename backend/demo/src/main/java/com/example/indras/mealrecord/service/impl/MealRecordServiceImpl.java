package com.example.indras.mealrecord.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.indras.common.enums.SourceType;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.NutritionCalculator;
import com.example.indras.common.vo.NutritionVO;
import com.example.indras.mealrecord.dto.MealRecordCreateRequest;
import com.example.indras.mealrecord.dto.MealRecordFromSourceRequest;
import com.example.indras.mealrecord.dto.MealRecordItemDTO;
import com.example.indras.mealrecord.entity.MealRecord;
import com.example.indras.mealrecord.entity.MealRecordItem;
import com.example.indras.mealrecord.mapper.MealRecordItemMapper;
import com.example.indras.mealrecord.mapper.MealRecordMapper;
import com.example.indras.mealrecord.service.MealRecordService;
import com.example.indras.mealrecord.vo.DailyMealRecordVO;
import com.example.indras.mealrecord.vo.MealRecordItemVO;
import com.example.indras.mealrecord.vo.MealRecordSummaryVO;
import com.example.indras.mealrecord.vo.MealRecordVO;
import com.example.indras.recipe.entity.Recipe;
import com.example.indras.recipe.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealRecordServiceImpl implements MealRecordService {

    private final MealRecordMapper mealRecordMapper;
    private final MealRecordItemMapper mealRecordItemMapper;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealRecordVO createManual(Long userId, MealRecordCreateRequest request) {
        NutritionCalculator.Nutrients total = NutritionCalculator.Nutrients.zero();
        for (MealRecordItemDTO item : request.getItems()) {
            total = NutritionCalculator.add(total, new NutritionCalculator.Nutrients(
                    item.getCalorie(), item.getProtein(), item.getFat(), item.getCarbohydrate(), BigDecimal.ZERO));
        }
        MealRecord record = MealRecord.builder()
                .userId(userId)
                .recordDate(request.getRecordDate())
                .mealType(request.getMealType())
                .sourceType(SourceType.MANUAL.name())
                .totalCalorie(total.calorie())
                .totalProtein(total.protein())
                .totalFat(total.fat())
                .totalCarbohydrate(total.carbohydrate())
                .remark(request.getRemark())
                .build();
        mealRecordMapper.insert(record);
        for (MealRecordItemDTO item : request.getItems()) {
            mealRecordItemMapper.insert(MealRecordItem.builder()
                    .mealRecordId(record.getId())
                    .foodName(item.getFoodName())
                    .amountG(item.getAmountG())
                    .calorie(item.getCalorie())
                    .protein(item.getProtein())
                    .fat(item.getFat())
                    .carbohydrate(item.getCarbohydrate())
                    .build());
        }
        return toVo(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealRecordVO fromRecipe(Long userId, Long recipeId, MealRecordFromSourceRequest request) {
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw BizException.notFound("菜谱不存在");
        }
        BigDecimal ratio = request.getServingRatio() == null ? BigDecimal.ONE : request.getServingRatio();
        MealRecord record = MealRecord.builder()
                .userId(userId)
                .recordDate(request.getRecordDate())
                .mealType(request.getMealType())
                .sourceType(SourceType.RECIPE.name())
                .sourceId(recipeId)
                .totalCalorie(scale(recipe.getTotalCalorie(), ratio))
                .totalProtein(scale(recipe.getTotalProtein(), ratio))
                .totalFat(scale(recipe.getTotalFat(), ratio))
                .totalCarbohydrate(scale(recipe.getTotalCarbohydrate(), ratio))
                .build();
        mealRecordMapper.insert(record);
        return toVo(record);
    }

    @Override
    public DailyMealRecordVO getDay(Long userId, LocalDate date) {
        List<MealRecord> records = mealRecordMapper.selectList(Wrappers.<MealRecord>lambdaQuery()
                .eq(MealRecord::getUserId, userId)
                .eq(MealRecord::getRecordDate, date));
        NutritionCalculator.Nutrients summary = NutritionCalculator.Nutrients.zero();
        for (MealRecord record : records) {
            summary = NutritionCalculator.add(summary, new NutritionCalculator.Nutrients(
                    record.getTotalCalorie(), record.getTotalProtein(), record.getTotalFat(), record.getTotalCarbohydrate(), BigDecimal.ZERO));
        }
        return DailyMealRecordVO.builder()
                .date(date)
                .records(records.stream().map(r -> {
                    MealRecordItem firstItem = mealRecordItemMapper.selectOne(Wrappers.<MealRecordItem>lambdaQuery()
                            .eq(MealRecordItem::getMealRecordId, r.getId())
                            .last("LIMIT 1"));
                    return MealRecordSummaryVO.builder()
                            .id(r.getId())
                            .mealType(r.getMealType())
                            .sourceType(r.getSourceType())
                            .foodName(firstItem != null ? firstItem.getFoodName() : r.getRemark())
                            .amountG(firstItem != null ? firstItem.getAmountG() : null)
                            .totalCalorie(r.getTotalCalorie())
                            .totalProtein(r.getTotalProtein())
                            .totalFat(r.getTotalFat())
                            .totalCarbohydrate(r.getTotalCarbohydrate())
                            .build();
                }).toList())
                .summary(NutritionVO.builder()
                        .calorie(summary.calorie())
                        .protein(summary.protein())
                        .fat(summary.fat())
                        .carbohydrate(summary.carbohydrate())
                        .build())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long userId, Long id) {
        MealRecord record = mealRecordMapper.selectById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw BizException.notFound("膳食记录不存在");
        }
        mealRecordItemMapper.delete(Wrappers.<MealRecordItem>lambdaQuery().eq(MealRecordItem::getMealRecordId, id));
        return mealRecordMapper.deleteById(id) > 0;
    }

    private BigDecimal scale(BigDecimal value, BigDecimal ratio) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
    }

    private MealRecordVO toVo(MealRecord record) {
        List<MealRecordItemVO> items = mealRecordItemMapper.selectList(
                        Wrappers.<MealRecordItem>lambdaQuery().eq(MealRecordItem::getMealRecordId, record.getId()))
                .stream()
                .map(i -> MealRecordItemVO.builder()
                        .foodName(i.getFoodName())
                        .amountG(i.getAmountG())
                        .calorie(i.getCalorie())
                        .protein(i.getProtein())
                        .fat(i.getFat())
                        .carbohydrate(i.getCarbohydrate())
                        .build())
                .toList();
        return MealRecordVO.builder()
                .id(record.getId())
                .recordDate(record.getRecordDate())
                .mealType(record.getMealType())
                .sourceType(record.getSourceType())
                .sourceId(record.getSourceId())
                .totalCalorie(record.getTotalCalorie())
                .totalProtein(record.getTotalProtein())
                .totalFat(record.getTotalFat())
                .totalCarbohydrate(record.getTotalCarbohydrate())
                .remark(record.getRemark())
                .items(items)
                .build();
    }
}
