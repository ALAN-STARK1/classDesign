package com.example.indras.mealplan.service;

import com.example.indras.common.vo.CountResultVO;
import com.example.indras.mealplan.dto.MealPlanFeedbackRequest;
import com.example.indras.mealplan.dto.MealPlanGenerateRequest;
import com.example.indras.mealplan.dto.MealPlanReplaceRequest;
import com.example.indras.mealplan.dto.MealPlanToRecordsRequest;
import com.example.indras.mealplan.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface MealPlanService {

    MealPlanVO generateDayPlan(Long userId, MealPlanGenerateRequest request);

    List<MealPlanVO> generateWeekPlan(Long userId, MealPlanGenerateRequest request);

    MealPlanVO getDayPlan(Long userId, LocalDate planDate);

    List<ReplacementCandidateVO> replacementCandidates(Long userId, Long planId, Long itemId, String reason, int limit);

    MealPlanVO replaceItem(Long userId, Long planId, Long itemId, MealPlanReplaceRequest request);

    List<MealPlanReplaceLogVO> replaceLogs(Long userId, Long planId);

    MealPlanFeedbackVO submitFeedback(Long userId, Long planId, Long itemId, MealPlanFeedbackRequest request);

    List<MealPlanFeedbackVO> listFeedback(Long userId, Long planId);

    MealPlanCompletionVO completion(Long userId, Long planId);

    CountResultVO toMealRecords(Long userId, Long planId, MealPlanToRecordsRequest request);

    ShoppingListVO shoppingList(Long userId, Long planId);

    ShoppingListVO weeklyShoppingList(Long userId, LocalDate startDate);

    WeeklyMealPosterVO weeklyPoster(Long userId, LocalDate startDate);
}
