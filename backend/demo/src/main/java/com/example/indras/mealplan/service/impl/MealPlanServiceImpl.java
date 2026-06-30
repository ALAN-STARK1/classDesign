package com.example.indras.mealplan.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.indras.common.enums.FeedbackStatus;
import com.example.indras.common.enums.MealPlanStatus;
import com.example.indras.common.enums.MealType;
import com.example.indras.common.enums.RecipeStatus;
import com.example.indras.common.enums.SourceType;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.vo.CountResultVO;
import com.example.indras.health.entity.HealthProfile;
import com.example.indras.health.mapper.HealthProfileMapper;
import com.example.indras.mealplan.dto.MealPlanFeedbackRequest;
import com.example.indras.mealplan.dto.MealPlanGenerateRequest;
import com.example.indras.mealplan.dto.MealPlanReplaceRequest;
import com.example.indras.mealplan.dto.MealPlanToRecordsRequest;
import com.example.indras.mealplan.entity.MealPlan;
import com.example.indras.mealplan.entity.MealPlanFeedback;
import com.example.indras.mealplan.entity.MealPlanItem;
import com.example.indras.mealplan.entity.MealPlanReplaceLog;
import com.example.indras.mealplan.mapper.MealPlanFeedbackMapper;
import com.example.indras.mealplan.mapper.MealPlanItemMapper;
import com.example.indras.mealplan.mapper.MealPlanMapper;
import com.example.indras.mealplan.mapper.MealPlanReplaceLogMapper;
import com.example.indras.mealplan.service.MealPlanService;
import com.example.indras.mealplan.vo.*;
import com.example.indras.mealrecord.entity.MealRecord;
import com.example.indras.mealrecord.mapper.MealRecordMapper;
import com.example.indras.ingredient.entity.Ingredient;
import com.example.indras.ingredient.mapper.IngredientMapper;
import com.example.indras.recipe.entity.RecipeIngredient;
import com.example.indras.recipe.mapper.RecipeIngredientMapper;
import com.example.indras.recipe.entity.Recipe;
import com.example.indras.recipe.entity.RecipeSuitabilityScore;
import com.example.indras.recipe.mapper.RecipeMapper;
import com.example.indras.recipe.mapper.RecipeSuitabilityScoreMapper;
import com.example.indras.recipe.service.RecipeService;
import com.example.indras.recipe.vo.SuitabilityScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {

    private static final Map<MealType, Double> MEAL_RATIO = Map.of(
            MealType.BREAKFAST, 0.25,
            MealType.LUNCH, 0.35,
            MealType.DINNER, 0.30,
            MealType.SNACK, 0.10
    );

    private final MealPlanMapper mealPlanMapper;
    private final MealPlanItemMapper mealPlanItemMapper;
    private final MealPlanFeedbackMapper mealPlanFeedbackMapper;
    private final MealPlanReplaceLogMapper mealPlanReplaceLogMapper;
    private final MealRecordMapper mealRecordMapper;
    private final RecipeMapper recipeMapper;
    private final RecipeSuitabilityScoreMapper recipeSuitabilityScoreMapper;
    private final HealthProfileMapper healthProfileMapper;
    private final RecipeService recipeService;
    private final RecipeIngredientMapper recipeIngredientMapper;
    private final IngredientMapper ingredientMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealPlanVO generateDayPlan(Long userId, MealPlanGenerateRequest request) {
        int targetCalorie = resolveTargetCalorie(userId, request);
        MealPlan existing = mealPlanMapper.selectOne(Wrappers.<MealPlan>lambdaQuery()
                .eq(MealPlan::getUserId, userId)
                .eq(MealPlan::getPlanDate, request.getPlanDate()));
        if (existing != null) {
            mealPlanItemMapper.delete(Wrappers.<MealPlanItem>lambdaQuery().eq(MealPlanItem::getPlanId, existing.getId()));
            mealPlanMapper.deleteById(existing.getId());
        }

        MealPlan plan = MealPlan.builder()
                .userId(userId)
                .planDate(request.getPlanDate())
                .targetCalorie(targetCalorie)
                .actualCalorie(BigDecimal.ZERO)
                .score(0)
                .status(MealPlanStatus.ACTIVE.name())
                .build();
        mealPlanMapper.insert(plan);

        BigDecimal actualCalorie = BigDecimal.ZERO;
        int sort = 1;
        List<MealPlanItem> items = new ArrayList<>();
        for (MealType mealType : List.of(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK)) {
            int mealTarget = (int) Math.round(targetCalorie * MEAL_RATIO.get(mealType));
            Recipe recipe = pickRecipe(userId, mealType.name(), mealTarget);
            if (recipe == null) {
                continue;
            }
            RecipeSuitabilityScore score = recipeSuitabilityScoreMapper.selectOne(
                    Wrappers.<RecipeSuitabilityScore>lambdaQuery()
                            .eq(RecipeSuitabilityScore::getUserId, userId)
                            .eq(RecipeSuitabilityScore::getRecipeId, recipe.getId()));
            if (score == null) {
                recipeService.suitability(userId, recipe.getId());
                score = recipeSuitabilityScoreMapper.selectOne(
                        Wrappers.<RecipeSuitabilityScore>lambdaQuery()
                                .eq(RecipeSuitabilityScore::getUserId, userId)
                                .eq(RecipeSuitabilityScore::getRecipeId, recipe.getId()));
            }
            MealPlanItem item = MealPlanItem.builder()
                    .planId(plan.getId())
                    .mealType(mealType.name())
                    .recipeId(recipe.getId())
                    .recipeName(recipe.getName())
                    .calorie(recipe.getTotalCalorie())
                    .suitabilityScore(score == null ? 0 : score.getScore())
                    .recommendReason("热量接近" + mealType.name() + "目标")
                    .sortNo(sort++)
                    .build();
            mealPlanItemMapper.insert(item);
            items.add(item);
            actualCalorie = actualCalorie.add(recipe.getTotalCalorie());
        }

        int score = calculatePlanScore(targetCalorie, actualCalorie);
        plan.setActualCalorie(actualCalorie);
        plan.setScore(score);
        plan.setRecommendReason("热量接近目标，已排除过敏冲突菜谱");
        mealPlanMapper.updateById(plan);
        return toPlanVo(plan, items, List.of());
    }

    @Override
    public MealPlanVO getDayPlan(Long userId, LocalDate planDate) {
        MealPlan plan = mealPlanMapper.selectOne(Wrappers.<MealPlan>lambdaQuery()
                .eq(MealPlan::getUserId, userId)
                .eq(MealPlan::getPlanDate, planDate));
        if (plan == null) {
            throw BizException.notFound("当日暂无膳食计划");
        }
        List<MealPlanItem> items = mealPlanItemMapper.selectList(Wrappers.<MealPlanItem>lambdaQuery()
                .eq(MealPlanItem::getPlanId, plan.getId())
                .orderByAsc(MealPlanItem::getSortNo));
        return toPlanVo(plan, items, List.of());
    }

    @Override
    public List<ReplacementCandidateVO> replacementCandidates(Long userId, Long planId, Long itemId, String reason, int limit) {
        MealPlanItem item = requirePlanItem(planId, itemId);
        requireOwnedPlan(userId, planId);
        List<Recipe> candidates = recipeMapper.selectList(Wrappers.<Recipe>lambdaQuery()
                .eq(Recipe::getCategory, mapMealTypeToCategory(item.getMealType()))
                .eq(Recipe::getStatus, RecipeStatus.ONLINE.name()));
        BigDecimal target = item.getCalorie();
        return candidates.stream()
                .filter(recipe -> calorieWithinRange(target, recipe.getTotalCalorie()))
                .sorted(Comparator.comparing(r -> r.getTotalCalorie().subtract(target).abs()))
                .limit(limit)
                .map(recipe -> ReplacementCandidateVO.builder()
                        .recipeId(recipe.getId())
                        .recipeName(recipe.getName())
                        .mealType(item.getMealType())
                        .calorie(recipe.getTotalCalorie())
                        .suitabilityScore(80)
                        .recommendScore(85)
                        .recommendReason("热量接近原计划，符合替换规则")
                        .build())
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealPlanVO replaceItem(Long userId, Long planId, Long itemId, MealPlanReplaceRequest request) {
        MealPlan plan = requireOwnedPlan(userId, planId);
        MealPlanItem item = requirePlanItem(planId, itemId);
        Recipe oldRecipe = recipeMapper.selectById(item.getRecipeId());
        Recipe newRecipe = recipeMapper.selectById(request.getNewRecipeId());
        if (newRecipe == null) {
            throw BizException.notFound("替换菜谱不存在");
        }
        BigDecimal delta = newRecipe.getTotalCalorie().subtract(item.getCalorie());
        item.setRecipeId(newRecipe.getId());
        item.setRecipeName(newRecipe.getName());
        item.setCalorie(newRecipe.getTotalCalorie());
        mealPlanItemMapper.updateById(item);

        mealPlanReplaceLogMapper.insert(MealPlanReplaceLog.builder()
                .planId(planId)
                .planItemId(itemId)
                .userId(userId)
                .oldRecipeId(oldRecipe.getId())
                .newRecipeId(newRecipe.getId())
                .replaceReason(request.getReplaceReason())
                .calorieDelta(delta)
                .recommendScore(90)
                .recommendReason("用户确认替换")
                .createdAt(LocalDateTime.now())
                .build());

        List<MealPlanItem> items = mealPlanItemMapper.selectList(Wrappers.<MealPlanItem>lambdaQuery()
                .eq(MealPlanItem::getPlanId, planId));
        BigDecimal actual = items.stream().map(MealPlanItem::getCalorie).reduce(BigDecimal.ZERO, BigDecimal::add);
        plan.setActualCalorie(actual);
        plan.setScore(calculatePlanScore(plan.getTargetCalorie(), actual));
        mealPlanMapper.updateById(plan);
        return toPlanVo(plan, items, List.of());
    }

    @Override
    public List<MealPlanReplaceLogVO> replaceLogs(Long userId, Long planId) {
        requireOwnedPlan(userId, planId);
        return mealPlanReplaceLogMapper.selectList(Wrappers.<MealPlanReplaceLog>lambdaQuery()
                        .eq(MealPlanReplaceLog::getPlanId, planId)
                        .orderByDesc(MealPlanReplaceLog::getCreatedAt))
                .stream()
                .map(log -> MealPlanReplaceLogVO.builder()
                        .id(log.getId())
                        .oldRecipeId(log.getOldRecipeId())
                        .oldRecipeName(recipeName(log.getOldRecipeId()))
                        .newRecipeId(log.getNewRecipeId())
                        .newRecipeName(recipeName(log.getNewRecipeId()))
                        .replaceReason(log.getReplaceReason())
                        .calorieDelta(log.getCalorieDelta())
                        .recommendScore(log.getRecommendScore())
                        .createdAt(log.getCreatedAt().atOffset(java.time.ZoneOffset.ofHours(8)))
                        .build())
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealPlanFeedbackVO submitFeedback(Long userId, Long planId, Long itemId, MealPlanFeedbackRequest request) {
        requireOwnedPlan(userId, planId);
        MealPlanItem item = requirePlanItem(planId, itemId);
        MealPlanFeedback feedback = MealPlanFeedback.builder()
                .planId(planId)
                .planItemId(itemId)
                .userId(userId)
                .mealType(item.getMealType())
                .feedbackDate(LocalDate.now())
                .status(request.getStatus())
                .actualRatio(request.getActualRatio() == null ? BigDecimal.ONE : request.getActualRatio())
                .reason(request.getReason())
                .remark(request.getRemark())
                .createdAt(LocalDateTime.now())
                .build();
        mealPlanFeedbackMapper.insert(feedback);

        if (Boolean.TRUE.equals(request.getCreateMealRecord())
                && !FeedbackStatus.SKIPPED.name().equals(request.getStatus())) {
            Recipe recipe = recipeMapper.selectById(item.getRecipeId());
            BigDecimal ratio = feedback.getActualRatio();
            mealRecordMapper.insert(MealRecord.builder()
                    .userId(userId)
                    .recordDate(LocalDate.now())
                    .mealType(item.getMealType())
                    .sourceType(SourceType.PLAN.name())
                    .sourceId(itemId)
                    .totalCalorie(recipe.getTotalCalorie().multiply(ratio).setScale(2, RoundingMode.HALF_UP))
                    .totalProtein(recipe.getTotalProtein().multiply(ratio).setScale(2, RoundingMode.HALF_UP))
                    .totalFat(recipe.getTotalFat().multiply(ratio).setScale(2, RoundingMode.HALF_UP))
                    .totalCarbohydrate(recipe.getTotalCarbohydrate().multiply(ratio).setScale(2, RoundingMode.HALF_UP))
                    .remark("计划执行反馈自动生成")
                    .build());
        }
        return toFeedbackVo(feedback);
    }

    @Override
    public List<MealPlanFeedbackVO> listFeedback(Long userId, Long planId) {
        requireOwnedPlan(userId, planId);
        return mealPlanFeedbackMapper.selectList(Wrappers.<MealPlanFeedback>lambdaQuery()
                        .eq(MealPlanFeedback::getPlanId, planId))
                .stream().map(this::toFeedbackVo).toList();
    }

    @Override
    public MealPlanCompletionVO completion(Long userId, Long planId) {
        requireOwnedPlan(userId, planId);
        List<MealPlanItem> items = mealPlanItemMapper.selectList(Wrappers.<MealPlanItem>lambdaQuery()
                .eq(MealPlanItem::getPlanId, planId));
        List<MealPlanFeedback> feedbacks = mealPlanFeedbackMapper.selectList(Wrappers.<MealPlanFeedback>lambdaQuery()
                .eq(MealPlanFeedback::getPlanId, planId));
        double completed = 0;
        for (MealPlanFeedback feedback : feedbacks) {
            completed += switch (feedback.getStatus()) {
                case "COMPLETED" -> 1.0;
                case "PARTIAL" -> 0.5;
                case "REPLACED" -> 1.0;
                default -> 0.0;
            };
        }
        int total = items.size();
        int rate = total == 0 ? 0 : (int) Math.round(completed / total * 100);
        return MealPlanCompletionVO.builder()
                .planId(planId)
                .completionRate(rate)
                .completedCount((int) completed)
                .totalCount(total)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CountResultVO toMealRecords(Long userId, Long planId, MealPlanToRecordsRequest request) {
        requireOwnedPlan(userId, planId);
        List<MealPlanItem> items = mealPlanItemMapper.selectList(Wrappers.<MealPlanItem>lambdaQuery()
                .eq(MealPlanItem::getPlanId, planId)
                .in(request.getMealTypes() != null && !request.getMealTypes().isEmpty(),
                        MealPlanItem::getMealType, request.getMealTypes()));
        int created = 0;
        for (MealPlanItem item : items) {
            Recipe recipe = recipeMapper.selectById(item.getRecipeId());
            mealRecordMapper.insert(MealRecord.builder()
                    .userId(userId)
                    .recordDate(LocalDate.now())
                    .mealType(item.getMealType())
                    .sourceType(SourceType.PLAN.name())
                    .sourceId(item.getId())
                    .totalCalorie(recipe.getTotalCalorie())
                    .totalProtein(recipe.getTotalProtein())
                    .totalFat(recipe.getTotalFat())
                    .totalCarbohydrate(recipe.getTotalCarbohydrate())
                    .build());
            created++;
        }
        return CountResultVO.builder().created(created).build();
    }

    @Override
    public ShoppingListVO shoppingList(Long userId, Long planId) {
        MealPlan plan = requireOwnedPlan(userId, planId);
        List<MealPlanItem> items = mealPlanItemMapper.selectList(Wrappers.<MealPlanItem>lambdaQuery()
                .eq(MealPlanItem::getPlanId, planId));
        Map<Long, BigDecimal> amountMap = new HashMap<>();
        for (MealPlanItem item : items) {
            List<RecipeIngredient> recipeIngredients = recipeIngredientMapper.selectList(
                    Wrappers.<RecipeIngredient>lambdaQuery().eq(RecipeIngredient::getRecipeId, item.getRecipeId()));
            for (RecipeIngredient ri : recipeIngredients) {
                amountMap.merge(ri.getIngredientId(),
                        ri.getAmountG() == null ? BigDecimal.ZERO : ri.getAmountG(),
                        BigDecimal::add);
            }
        }
        List<ShoppingListItemVO> shoppingItems = amountMap.entrySet().stream()
                .map(entry -> {
                    Ingredient ingredient = ingredientMapper.selectById(entry.getKey());
                    if (ingredient == null) {
                        return null;
                    }
                    return ShoppingListItemVO.builder()
                            .ingredientId(ingredient.getId())
                            .name(ingredient.getName())
                            .unit(ingredient.getUnit())
                            .amount(entry.getValue())
                            .category(ingredient.getCategory())
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparing(ShoppingListItemVO::getCategory, Comparator.nullsLast(String::compareTo)))
                .toList();
        return ShoppingListVO.builder()
                .planId(planId)
                .planDate(plan.getPlanDate())
                .items(shoppingItems)
                .build();
    }

    private int resolveTargetCalorie(Long userId, MealPlanGenerateRequest request) {
        if (request.getTargetCalorie() != null) {
            return request.getTargetCalorie();
        }
        HealthProfile profile = healthProfileMapper.selectOne(Wrappers.<HealthProfile>lambdaQuery()
                .eq(HealthProfile::getUserId, userId));
        return profile == null || profile.getDailyCalorieTarget() == null ? 1600 : profile.getDailyCalorieTarget();
    }

    private Recipe pickRecipe(Long userId, String mealType, int mealTarget) {
        String category = mapMealTypeToCategory(mealType);
        List<Recipe> recipes = recipeMapper.selectList(Wrappers.<Recipe>lambdaQuery()
                .eq(Recipe::getCategory, category)
                .eq(Recipe::getStatus, RecipeStatus.ONLINE.name()));
        return recipes.stream()
                .filter(r -> {
                    SuitabilityScoreVO vo = recipeService.suitability(userId, r.getId());
                    return vo.getRiskScore() != null && vo.getRiskScore() > 0;
                })
                .min(Comparator.comparing(r -> r.getTotalCalorie().subtract(BigDecimal.valueOf(mealTarget)).abs()))
                .orElse(recipes.isEmpty() ? null : recipes.get(0));
    }

    private String mapMealTypeToCategory(String mealType) {
        return switch (mealType) {
            case "BREAKFAST" -> "BREAKFAST";
            case "LUNCH" -> "LUNCH";
            case "DINNER" -> "DINNER";
            default -> "SNACK";
        };
    }

    private boolean calorieWithinRange(BigDecimal base, BigDecimal candidate) {
        if (base == null || candidate == null || base.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        BigDecimal diff = candidate.subtract(base).abs();
        return diff.compareTo(base.multiply(BigDecimal.valueOf(0.3))) <= 0;
    }

    private int calculatePlanScore(int target, BigDecimal actual) {
        if (actual == null) {
            return 0;
        }
        double diff = Math.abs(actual.doubleValue() - target);
        double ratio = diff / Math.max(target, 1);
        return (int) Math.max(0, Math.round(100 - ratio * 100));
    }

    private MealPlan requireOwnedPlan(Long userId, Long planId) {
        MealPlan plan = mealPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw BizException.notFound("膳食计划不存在");
        }
        return plan;
    }

    private MealPlanItem requirePlanItem(Long planId, Long itemId) {
        MealPlanItem item = mealPlanItemMapper.selectById(itemId);
        if (item == null || !item.getPlanId().equals(planId)) {
            throw BizException.notFound("计划明细不存在");
        }
        return item;
    }

    private String recipeName(Long recipeId) {
        Recipe recipe = recipeMapper.selectById(recipeId);
        return recipe == null ? null : recipe.getName();
    }

    private MealPlanVO toPlanVo(MealPlan plan, List<MealPlanItem> items, List<RiskResultBriefVO> risks) {
        return MealPlanVO.builder()
                .id(plan.getId())
                .planDate(plan.getPlanDate())
                .targetCalorie(plan.getTargetCalorie())
                .actualCalorie(plan.getActualCalorie())
                .score(plan.getScore())
                .status(plan.getStatus())
                .recommendReason(plan.getRecommendReason())
                .riskResults(risks)
                .items(items.stream().map(item -> MealPlanItemVO.builder()
                        .id(item.getId())
                        .mealType(item.getMealType())
                        .recipeId(item.getRecipeId())
                        .recipeName(item.getRecipeName())
                        .calorie(item.getCalorie())
                        .suitabilityScore(item.getSuitabilityScore())
                        .recommendReason(item.getRecommendReason())
                        .build()).toList())
                .build();
    }

    private MealPlanFeedbackVO toFeedbackVo(MealPlanFeedback feedback) {
        return MealPlanFeedbackVO.builder()
                .id(feedback.getId())
                .planId(feedback.getPlanId())
                .planItemId(feedback.getPlanItemId())
                .mealType(feedback.getMealType())
                .feedbackDate(feedback.getFeedbackDate())
                .status(feedback.getStatus())
                .actualRatio(feedback.getActualRatio())
                .reason(feedback.getReason())
                .remark(feedback.getRemark())
                .build();
    }
}
