package com.example.indras.mealrecord.service;

import com.example.indras.mealrecord.dto.MealRecordCreateRequest;
import com.example.indras.mealrecord.dto.MealRecordFromSourceRequest;
import com.example.indras.mealrecord.vo.DailyMealRecordVO;
import com.example.indras.mealrecord.vo.MealRecordVO;

import java.time.LocalDate;

public interface MealRecordService {

    MealRecordVO createManual(Long userId, MealRecordCreateRequest request);

    MealRecordVO fromRecipe(Long userId, Long recipeId, MealRecordFromSourceRequest request);

    DailyMealRecordVO getDay(Long userId, LocalDate date);

    boolean delete(Long userId, Long id);
}
