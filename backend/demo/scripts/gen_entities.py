from pathlib import Path

BASE = Path(__file__).resolve().parents[1] / "src/main/java/com/example/indras"

entities = [
    ("user/entity", "SysUser", "sys_user", [
        ("id", "Long"), ("username", "String"), ("email", "String"), ("passwordHash", "String", "password_hash"),
        ("role", "String"), ("status", "String"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("user/entity", "UserProfile", "user_profile", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("nickname", "String"), ("avatarUrl", "String", "avatar_url"),
        ("bio", "String"), ("gender", "String"), ("birthday", "LocalDate"), ("createdAt", "LocalDateTime", "created_at"),
        ("updatedAt", "LocalDateTime", "updated_at"),
    ]),
    ("health/entity", "HealthProfile", "health_profile", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("gender", "String"), ("birthday", "LocalDate"),
        ("heightCm", "BigDecimal", "height_cm"), ("weightKg", "BigDecimal", "weight_kg"),
        ("targetWeightKg", "BigDecimal", "target_weight_kg"), ("activityLevel", "String", "activity_level"),
        ("healthGoal", "String", "health_goal"), ("bmi", "BigDecimal"), ("dailyCalorieTarget", "Integer", "daily_calorie_target"),
    ]),
    ("health/entity", "UserPreference", "user_preference", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("likedTags", "String", "liked_tags"),
        ("dislikedIngredients", "String", "disliked_ingredients"), ("preferredCuisines", "String", "preferred_cuisines"),
        ("spiceLevel", "String", "spice_level"), ("createdAt", "LocalDateTime", "created_at"), ("updatedAt", "LocalDateTime", "updated_at"),
    ]),
    ("health/entity", "UserAllergen", "user_allergen", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("allergenName", "String", "allergen_name"),
    ]),
    ("health/entity", "UserDietRestriction", "user_diet_restriction", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("restrictionName", "String", "restriction_name"),
    ]),
    ("health/entity", "WeightRecord", "weight_record", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("recordDate", "LocalDate", "record_date"),
        ("weightKg", "BigDecimal", "weight_kg"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("healthgoal/entity", "HealthGoalCycle", "health_goal_cycle", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("goalType", "String", "goal_type"),
        ("startDate", "LocalDate", "start_date"), ("endDate", "LocalDate", "end_date"),
        ("startWeightKg", "BigDecimal", "start_weight_kg"), ("targetWeightKg", "BigDecimal", "target_weight_kg"),
        ("targetCalorie", "Integer", "target_calorie"), ("weeklyTargetDeltaKg", "BigDecimal", "weekly_target_delta_kg"),
        ("progressPercent", "BigDecimal", "progress_percent"), ("status", "String"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("ingredient/entity", "Ingredient", "ingredient", [
        ("id", "Long"), ("name", "String"), ("category", "String"), ("unit", "String"),
        ("calorie", "BigDecimal"), ("protein", "BigDecimal"), ("fat", "BigDecimal"),
        ("carbohydrate", "BigDecimal"), ("sodium", "BigDecimal"), ("status", "String"),
    ]),
    ("recipe/entity", "Recipe", "recipe", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("name", "String"), ("description", "String"),
        ("category", "String"), ("difficulty", "String"), ("cookMinutes", "Integer", "cook_minutes"),
        ("servings", "Integer"), ("totalCalorie", "BigDecimal", "total_calorie"), ("totalProtein", "BigDecimal", "total_protein"),
        ("totalFat", "BigDecimal", "total_fat"), ("totalCarbohydrate", "BigDecimal", "total_carbohydrate"), ("status", "String"),
    ]),
    ("recipe/entity", "RecipeIngredient", "recipe_ingredient", [
        ("id", "Long"), ("recipeId", "Long", "recipe_id"), ("ingredientId", "Long", "ingredient_id"),
        ("amountG", "BigDecimal", "amount_g"), ("remark", "String"),
    ]),
    ("recipe/entity", "RecipeStep", "recipe_step", [
        ("id", "Long"), ("recipeId", "Long", "recipe_id"), ("stepNo", "Integer", "step_no"), ("content", "String"),
    ]),
    ("recipe/entity", "RecipeFavorite", "recipe_favorite", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("recipeId", "Long", "recipe_id"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("recipe/entity", "RecipeSuitabilityScore", "recipe_suitability_score", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("recipeId", "Long", "recipe_id"), ("score", "Integer"),
        ("calorieScore", "Integer", "calorie_score"), ("macroScore", "Integer", "macro_score"),
        ("preferenceScore", "Integer", "preference_score"), ("riskScore", "Integer", "risk_score"),
        ("reason", "String"), ("calculatedAt", "LocalDateTime", "calculated_at"),
    ]),
    ("mealplan/entity", "MealPlan", "meal_plan", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("planDate", "LocalDate", "plan_date"),
        ("targetCalorie", "Integer", "target_calorie"), ("actualCalorie", "BigDecimal", "actual_calorie"),
        ("score", "Integer"), ("recommendReason", "String", "recommend_reason"), ("status", "String"),
    ]),
    ("mealplan/entity", "MealPlanItem", "meal_plan_item", [
        ("id", "Long"), ("planId", "Long", "plan_id"), ("mealType", "String", "meal_type"),
        ("recipeId", "Long", "recipe_id"), ("recipeName", "String", "recipe_name"), ("calorie", "BigDecimal"),
        ("suitabilityScore", "Integer", "suitability_score"), ("recommendReason", "String", "recommend_reason"), ("sortNo", "Integer", "sort_no"),
    ]),
    ("mealplan/entity", "MealPlanFeedback", "meal_plan_feedback", [
        ("id", "Long"), ("planId", "Long", "plan_id"), ("planItemId", "Long", "plan_item_id"), ("userId", "Long", "user_id"),
        ("mealType", "String", "meal_type"), ("feedbackDate", "LocalDate", "feedback_date"), ("status", "String"),
        ("actualRatio", "BigDecimal", "actual_ratio"), ("reason", "String"), ("remark", "String"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("mealplan/entity", "MealPlanReplaceLog", "meal_plan_replace_log", [
        ("id", "Long"), ("planId", "Long", "plan_id"), ("planItemId", "Long", "plan_item_id"), ("userId", "Long", "user_id"),
        ("oldRecipeId", "Long", "old_recipe_id"), ("newRecipeId", "Long", "new_recipe_id"), ("replaceReason", "String", "replace_reason"),
        ("calorieDelta", "BigDecimal", "calorie_delta"), ("recommendScore", "Integer", "recommend_score"),
        ("recommendReason", "String", "recommend_reason"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("mealrecord/entity", "MealRecord", "meal_record", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("recordDate", "LocalDate", "record_date"), ("mealType", "String", "meal_type"),
        ("sourceType", "String", "source_type"), ("sourceId", "Long", "source_id"), ("totalCalorie", "BigDecimal", "total_calorie"),
        ("totalProtein", "BigDecimal", "total_protein"), ("totalFat", "BigDecimal", "total_fat"),
        ("totalCarbohydrate", "BigDecimal", "total_carbohydrate"), ("remark", "String"),
    ]),
    ("mealrecord/entity", "MealRecordItem", "meal_record_item", [
        ("id", "Long"), ("mealRecordId", "Long", "meal_record_id"), ("foodName", "String", "food_name"), ("amountG", "BigDecimal", "amount_g"),
        ("calorie", "BigDecimal"), ("protein", "BigDecimal"), ("fat", "BigDecimal"), ("carbohydrate", "BigDecimal"),
    ]),
    ("risk/entity", "NutritionRiskRule", "nutrition_risk_rule", [
        ("id", "Long"), ("ruleCode", "String", "rule_code"), ("ruleName", "String", "rule_name"), ("scenario", "String"),
        ("nutrient", "String"), ("operator", "String"), ("thresholdMin", "BigDecimal", "threshold_min"),
        ("thresholdMax", "BigDecimal", "threshold_max"), ("severity", "String"), ("message", "String"), ("status", "String"),
    ]),
    ("risk/entity", "NutritionRiskResult", "nutrition_risk_result", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("ruleId", "Long", "rule_id"), ("sourceType", "String", "source_type"),
        ("sourceId", "Long", "source_id"), ("riskDate", "LocalDate", "risk_date"), ("severity", "String"),
        ("message", "String"), ("suggestion", "String"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("report/entity", "NutritionReport", "nutrition_report", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("reportType", "String", "report_type"), ("startDate", "LocalDate", "start_date"),
        ("endDate", "LocalDate", "end_date"), ("avgCalorie", "BigDecimal", "avg_calorie"), ("avgProtein", "BigDecimal", "avg_protein"),
        ("avgFat", "BigDecimal", "avg_fat"), ("avgCarbohydrate", "BigDecimal", "avg_carbohydrate"), ("targetDays", "Integer", "target_days"),
        ("recordDays", "Integer", "record_days"), ("completionRate", "BigDecimal", "completion_rate"), ("riskCount", "Integer", "risk_count"),
        ("summary", "String"), ("suggestionsJson", "String", "suggestions_json"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("airecipe/entity", "AiRecipe", "ai_recipe", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("sourceType", "String", "source_type"), ("recipeName", "String", "recipe_name"),
        ("description", "String"), ("recognizedFoodsJson", "String", "recognized_foods_json"), ("ingredientsJson", "String", "ingredients_json"),
        ("nutritionJson", "String", "nutrition_json"), ("suitabilityScore", "Integer", "suitability_score"),
        ("suitabilityReason", "String", "suitability_reason"), ("healthTipsJson", "String", "health_tips_json"),
        ("warningsJson", "String", "warnings_json"), ("rawResponseJson", "String", "raw_response_json"),
        ("sourceImageUrl", "String", "source_image_url"), ("sourceImageKey", "String", "source_image_key"), ("status", "String"),
        ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("airecipe/entity", "AiRecipeStep", "ai_recipe_step", [
        ("id", "Long"), ("aiRecipeId", "Long", "ai_recipe_id"), ("stepNo", "Integer", "step_no"), ("content", "String"),
    ]),
    ("airecipe/entity", "AiCallLog", "ai_call_log", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("scene", "String"), ("requestSummary", "String", "request_summary"),
        ("elapsedMs", "Integer", "elapsed_ms"), ("success", "Boolean"), ("errorMessage", "String", "error_message"),
        ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("community/entity", "CommunityPost", "community_post", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("title", "String"), ("content", "String"), ("recipeName", "String", "recipe_name"),
        ("tagsJson", "String", "tags_json"), ("sourceType", "String", "source_type"), ("sourceId", "Long", "source_id"),
        ("status", "String"), ("likeCount", "Integer", "like_count"), ("commentCount", "Integer", "comment_count"),
        ("favoriteCount", "Integer", "favorite_count"), ("publishedAt", "LocalDateTime", "published_at"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("community/entity", "CommunityPostImage", "community_post_image", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("postId", "Long", "post_id"), ("storageKey", "String", "storage_key"),
        ("imageUrl", "String", "image_url"), ("sortNo", "Integer", "sort_no"), ("width", "Integer"), ("height", "Integer"),
        ("fileSize", "Long", "file_size"), ("status", "String"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("community/entity", "CommunityComment", "community_comment", [
        ("id", "Long"), ("postId", "Long", "post_id"), ("userId", "Long", "user_id"), ("content", "String"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("community/entity", "CommunityLike", "community_like", [
        ("id", "Long"), ("postId", "Long", "post_id"), ("userId", "Long", "user_id"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("community/entity", "CommunityFavorite", "community_favorite", [
        ("id", "Long"), ("postId", "Long", "post_id"), ("userId", "Long", "user_id"), ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("admin/entity", "Notification", "notification", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("type", "String"), ("title", "String"), ("content", "String"),
        ("readFlag", "Boolean", "read_flag"), ("relatedType", "String", "related_type"), ("relatedId", "Long", "related_id"),
        ("createdAt", "LocalDateTime", "created_at"),
    ]),
    ("admin/entity", "AccessLog", "access_log", [
        ("id", "Long"), ("userId", "Long", "user_id"), ("method", "String"), ("path", "String"), ("ip", "String"),
        ("elapsedMs", "Integer", "elapsed_ms"), ("statusCode", "Integer", "status_code"), ("bizCode", "Integer", "biz_code"),
        ("createdAt", "LocalDateTime", "created_at"),
    ]),
]

LOB_FIELDS = {
    "CommunityPost": {"content"},
    "NutritionReport": {"suggestionsJson"},
    "AiRecipe": {"recognizedFoodsJson", "ingredientsJson", "nutritionJson", "healthTipsJson", "warningsJson", "rawResponseJson"},
}

imports_map = {
    "BigDecimal": "java.math.BigDecimal",
    "LocalDate": "java.time.LocalDate",
    "LocalDateTime": "java.time.LocalDateTime",
}

for pkg_suffix, cls, table, fields in entities:
    pkg = f"com.example.indras.{pkg_suffix.replace('/', '.')}"
    used = set()
    field_lines = []
    for f in fields:
        if len(f) == 2:
            name, jtype = f
            col = None
        else:
            name, jtype, col = f
        if jtype in imports_map:
            used.add(jtype)
        if name == "id":
            field_lines.append("    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;")
            continue
        lines = []
        if col:
            lines.append(f'    @Column(name = "{col}")')
        if cls in LOB_FIELDS and name in LOB_FIELDS[cls]:
            lines.append("    @Lob")
        lines.append(f"    private {jtype} {name};")
        field_lines.append("\n".join(lines))

    import_lines = [f"import {imports_map[t]};" for t in sorted(used)]
    content = f"""package {pkg};

import jakarta.persistence.*;
import lombok.*;
{chr(10).join(import_lines)}

@Entity
@Table(name = "{table}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class {cls} {{

{chr(10).join(field_lines)}
}}
"""
    path = BASE / pkg_suffix / f"{cls}.java"
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")
    print(f"Created {path.name}")

print(f"Total entities: {len(entities)}")
