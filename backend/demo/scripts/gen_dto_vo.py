from pathlib import Path

BASE = Path(__file__).resolve().parents[1] / "src/main/java/com/example/indras"

# (package, class_name, kind[dto|vo], fields as (type, name) or (type, name, validation))
CLASSES = [
    # auth
    ("auth/dto", "RegisterRequest", "dto", [
        ("String", "username", "@NotBlank @Size(min=4,max=50)"),
        ("String", "email", "@NotBlank @Email"),
        ("String", "password", "@NotBlank @Size(min=6,max=32)"),
    ]),
    ("auth/dto", "LoginRequest", "dto", [
        ("String", "username", "@NotBlank"),
        ("String", "password", "@NotBlank"),
    ]),
    ("auth/dto", "ChangePasswordRequest", "dto", [
        ("String", "oldPassword", "@NotBlank"),
        ("String", "newPassword", "@NotBlank @Size(min=6,max=32)"),
    ]),
    ("auth/vo", "RegisterResponseVO", "vo", [
        ("Long", "id"), ("String", "username"), ("String", "email"),
        ("String", "role"), ("String", "status"),
    ]),
    ("auth/vo", "LoginResponseVO", "vo", [
        ("String", "token"), ("String", "tokenType"), ("Integer", "expiresIn"), ("UserVO", "user"),
    ]),
    ("auth/vo", "UserVO", "vo", [
        ("Long", "id"), ("String", "username"), ("String", "email"), ("String", "role"),
    ]),
    # health
    ("health/dto", "HealthProfileSaveRequest", "dto", [
        ("String", "gender"), ("LocalDate", "birthday"), ("BigDecimal", "heightCm"),
        ("BigDecimal", "weightKg"), ("BigDecimal", "targetWeightKg"),
        ("String", "activityLevel"), ("String", "healthGoal"),
    ]),
    ("health/dto", "AllergensUpdateRequest", "dto", [
        ("List<String>", "allergens", "@NotNull"),
    ]),
    ("health/dto", "RestrictionsUpdateRequest", "dto", [
        ("List<String>", "restrictions", "@NotNull"),
    ]),
    ("health/vo", "HealthProfileVO", "vo", [
        ("Long", "id"), ("String", "gender"), ("LocalDate", "birthday"),
        ("BigDecimal", "heightCm"), ("BigDecimal", "weightKg"), ("BigDecimal", "targetWeightKg"),
        ("String", "activityLevel"), ("String", "healthGoal"), ("BigDecimal", "bmi"),
        ("Integer", "dailyCalorieTarget"), ("List<String>", "allergens"), ("List<String>", "restrictions"),
    ]),
    ("health/vo", "HealthProfileSummaryVO", "vo", [
        ("BigDecimal", "bmi"), ("String", "bmiStatus"), ("Integer", "bmr"),
        ("Integer", "tdee"), ("Integer", "dailyCalorieTarget"),
    ]),
    # healthgoal
    ("healthgoal/dto", "HealthGoalCycleCreateRequest", "dto", [
        ("String", "goalType", "@NotBlank"), ("LocalDate", "startDate", "@NotNull"),
        ("LocalDate", "endDate", "@NotNull"), ("BigDecimal", "startWeightKg", "@NotNull"),
        ("BigDecimal", "targetWeightKg", "@NotNull"), ("Integer", "targetCalorie", "@NotNull"),
    ]),
    ("healthgoal/vo", "HealthGoalCycleVO", "vo", [
        ("Long", "id"), ("String", "goalType"), ("LocalDate", "startDate"), ("LocalDate", "endDate"),
        ("BigDecimal", "startWeightKg"), ("BigDecimal", "targetWeightKg"), ("Integer", "targetCalorie"),
        ("BigDecimal", "weeklyTargetDeltaKg"), ("BigDecimal", "progressPercent"), ("String", "status"),
    ]),
    ("healthgoal/vo", "HealthGoalProgressVO", "vo", [
        ("Long", "cycleId"), ("BigDecimal", "progressPercent"), ("BigDecimal", "currentWeightKg"),
        ("BigDecimal", "expectedWeightKg"), ("BigDecimal", "deviationKg"), ("Boolean", "warning"),
        ("List<WeightTrendPointVO>", "trend"),
    ]),
    ("healthgoal/vo", "WeightTrendPointVO", "vo", [
        ("LocalDate", "date"), ("BigDecimal", "weightKg"),
    ]),
    ("healthgoal/vo", "HealthGoalCompleteVO", "vo", [
        ("Long", "id"), ("String", "status"), ("String", "summary"),
    ]),
    # ingredient
    ("ingredient/dto", "IngredientSaveRequest", "dto", [
        ("String", "name", "@NotBlank"), ("String", "category", "@NotBlank"), ("String", "unit"),
        ("BigDecimal", "calorie"), ("BigDecimal", "protein"), ("BigDecimal", "fat"),
        ("BigDecimal", "carbohydrate"), ("BigDecimal", "sodium"), ("String", "status"),
    ]),
    ("ingredient/vo", "IngredientVO", "vo", [
        ("Long", "id"), ("String", "name"), ("String", "category"), ("String", "unit"),
        ("BigDecimal", "calorie"), ("BigDecimal", "protein"), ("BigDecimal", "fat"),
        ("BigDecimal", "carbohydrate"), ("BigDecimal", "sodium"), ("String", "status"),
    ]),
    # recipe
    ("recipe/dto", "RecipeCreateRequest", "dto", [
        ("String", "name", "@NotBlank"), ("String", "description"), ("String", "category", "@NotBlank"),
        ("String", "difficulty"), ("Integer", "cookMinutes"), ("Integer", "servings"),
        ("List<RecipeStepDTO>", "steps"),
    ]),
    ("recipe/dto", "RecipeStepDTO", "dto", [
        ("Integer", "stepNo"), ("String", "content"),
    ]),
    ("recipe/dto", "RecipeIngredientBindRequest", "dto", [
        ("List<RecipeIngredientItemDTO>", "ingredients", "@NotNull"),
    ]),
    ("recipe/dto", "RecipeIngredientItemDTO", "dto", [
        ("Long", "ingredientId"), ("BigDecimal", "amountG"), ("String", "remark"),
    ]),
    ("recipe/dto", "SuitabilityRecalculateRequest", "dto", [
        ("List<Long>", "recipeIds"),
    ]),
    ("recipe/vo", "RecipeListItemVO", "vo", [
        ("Long", "id"), ("String", "name"), ("String", "description"), ("String", "category"),
        ("String", "difficulty"), ("Integer", "cookMinutes"), ("Integer", "servings"),
        ("BigDecimal", "totalCalorie"), ("BigDecimal", "totalProtein"), ("BigDecimal", "totalFat"),
        ("BigDecimal", "totalCarbohydrate"), ("String", "status"), ("Boolean", "favorite"),
        ("Integer", "suitabilityScore"),
    ]),
    ("recipe/vo", "RecipeDetailVO", "vo", [
        ("Long", "id"), ("String", "name"), ("String", "description"), ("String", "category"),
        ("String", "difficulty"), ("Integer", "cookMinutes"), ("Integer", "servings"),
        ("BigDecimal", "totalCalorie"), ("BigDecimal", "totalProtein"), ("BigDecimal", "totalFat"),
        ("BigDecimal", "totalCarbohydrate"), ("String", "status"),
        ("List<RecipeIngredientVO>", "ingredients"), ("List<RecipeStepVO>", "steps"),
    ]),
    ("recipe/vo", "RecipeIngredientVO", "vo", [
        ("Long", "ingredientId"), ("String", "name"), ("BigDecimal", "amountG"), ("String", "remark"),
    ]),
    ("recipe/vo", "RecipeStepVO", "vo", [
        ("Integer", "stepNo"), ("String", "content"),
    ]),
    ("recipe/vo", "SuitabilityScoreVO", "vo", [
        ("Long", "recipeId"), ("Integer", "score"), ("Integer", "calorieScore"),
        ("Integer", "macroScore"), ("Integer", "preferenceScore"), ("Integer", "riskScore"), ("String", "reason"),
    ]),
    ("recipe/vo", "RecipeNutritionVO", "vo", [
        ("Long", "recipeId"), ("BigDecimal", "totalCalorie"), ("BigDecimal", "totalProtein"),
        ("BigDecimal", "totalFat"), ("BigDecimal", "totalCarbohydrate"),
    ]),
    # mealplan
    ("mealplan/dto", "MealPlanGenerateRequest", "dto", [
        ("LocalDate", "planDate", "@NotNull"), ("Integer", "targetCalorie"), ("Long", "goalCycleId"),
    ]),
    ("mealplan/dto", "MealPlanReplaceRequest", "dto", [
        ("Long", "newRecipeId", "@NotNull"), ("String", "replaceReason"), ("String", "remark"),
    ]),
    ("mealplan/dto", "MealPlanFeedbackRequest", "dto", [
        ("String", "status", "@NotBlank"), ("BigDecimal", "actualRatio"), ("String", "reason"),
        ("String", "remark"), ("Boolean", "createMealRecord"),
    ]),
    ("mealplan/dto", "MealPlanToRecordsRequest", "dto", [
        ("List<String>", "mealTypes"),
    ]),
    ("mealplan/vo", "MealPlanVO", "vo", [
        ("Long", "id"), ("LocalDate", "planDate"), ("Integer", "targetCalorie"),
        ("BigDecimal", "actualCalorie"), ("Integer", "score"), ("String", "status"),
        ("String", "recommendReason"), ("List<RiskResultBriefVO>", "riskResults"), ("List<MealPlanItemVO>", "items"),
    ]),
    ("mealplan/vo", "MealPlanItemVO", "vo", [
        ("Long", "id"), ("String", "mealType"), ("Long", "recipeId"), ("String", "recipeName"),
        ("BigDecimal", "calorie"), ("Integer", "suitabilityScore"), ("String", "recommendReason"),
    ]),
    ("mealplan/vo", "ReplacementCandidateVO", "vo", [
        ("Long", "recipeId"), ("String", "recipeName"), ("String", "mealType"), ("BigDecimal", "calorie"),
        ("Integer", "suitabilityScore"), ("Integer", "recommendScore"), ("String", "recommendReason"),
    ]),
    ("mealplan/vo", "MealPlanFeedbackVO", "vo", [
        ("Long", "id"), ("Long", "planId"), ("Long", "planItemId"), ("String", "mealType"),
        ("LocalDate", "feedbackDate"), ("String", "status"), ("BigDecimal", "actualRatio"),
        ("String", "reason"), ("String", "remark"),
    ]),
    ("mealplan/vo", "MealPlanReplaceLogVO", "vo", [
        ("Long", "id"), ("Long", "oldRecipeId"), ("String", "oldRecipeName"), ("Long", "newRecipeId"),
        ("String", "newRecipeName"), ("String", "replaceReason"), ("BigDecimal", "calorieDelta"),
        ("Integer", "recommendScore"), ("OffsetDateTime", "createdAt"),
    ]),
    ("mealplan/vo", "MealPlanCompletionVO", "vo", [
        ("Long", "planId"), ("Integer", "completionRate"), ("Integer", "completedCount"), ("Integer", "totalCount"),
    ]),
    ("mealplan/vo", "RiskResultBriefVO", "vo", [
        ("String", "severity"), ("String", "message"),
    ]),
    # mealrecord
    ("mealrecord/dto", "MealRecordCreateRequest", "dto", [
        ("LocalDate", "recordDate", "@NotNull"), ("String", "mealType", "@NotBlank"), ("String", "remark"),
        ("List<MealRecordItemDTO>", "items", "@NotNull"),
    ]),
    ("mealrecord/dto", "MealRecordItemDTO", "dto", [
        ("String", "foodName"), ("BigDecimal", "amountG"), ("BigDecimal", "calorie"),
        ("BigDecimal", "protein"), ("BigDecimal", "fat"), ("BigDecimal", "carbohydrate"),
    ]),
    ("mealrecord/dto", "MealRecordFromSourceRequest", "dto", [
        ("LocalDate", "recordDate", "@NotNull"), ("String", "mealType", "@NotBlank"), ("BigDecimal", "servingRatio"),
    ]),
    ("mealrecord/vo", "MealRecordVO", "vo", [
        ("Long", "id"), ("LocalDate", "recordDate"), ("String", "mealType"), ("String", "sourceType"),
        ("Long", "sourceId"), ("BigDecimal", "totalCalorie"), ("BigDecimal", "totalProtein"),
        ("BigDecimal", "totalFat"), ("BigDecimal", "totalCarbohydrate"), ("String", "remark"),
        ("List<MealRecordItemVO>", "items"),
    ]),
    ("mealrecord/vo", "MealRecordItemVO", "vo", [
        ("String", "foodName"), ("BigDecimal", "amountG"), ("BigDecimal", "calorie"),
        ("BigDecimal", "protein"), ("BigDecimal", "fat"), ("BigDecimal", "carbohydrate"),
    ]),
    ("mealrecord/vo", "DailyMealRecordVO", "vo", [
        ("LocalDate", "date"), ("List<MealRecordSummaryVO>", "records"), ("NutritionVO", "summary"),
    ]),
    ("mealrecord/vo", "MealRecordSummaryVO", "vo", [
        ("Long", "id"), ("String", "mealType"), ("String", "sourceType"),
        ("BigDecimal", "totalCalorie"), ("BigDecimal", "totalProtein"),
        ("BigDecimal", "totalFat"), ("BigDecimal", "totalCarbohydrate"),
    ]),
    # nutrition
    ("nutrition/vo", "TodayNutritionVO", "vo", [
        ("LocalDate", "date"), ("Integer", "targetCalorie"), ("BigDecimal", "totalCalorie"),
        ("Integer", "caloriePercent"), ("BigDecimal", "protein"), ("BigDecimal", "fat"),
        ("BigDecimal", "carbohydrate"), ("MacroRatioVO", "macroRatio"),
    ]),
    ("nutrition/vo", "NutritionRangeVO", "vo", [
        ("LocalDate", "startDate"), ("LocalDate", "endDate"), ("List<DailyNutritionPointVO>", "days"),
    ]),
    ("nutrition/vo", "DailyNutritionPointVO", "vo", [
        ("LocalDate", "date"), ("BigDecimal", "calorie"), ("BigDecimal", "protein"),
        ("BigDecimal", "fat"), ("BigDecimal", "carbohydrate"),
    ]),
    ("nutrition/vo", "NutritionGapVO", "vo", [
        ("LocalDate", "date"), ("Integer", "calorieGap"), ("Integer", "proteinGap"),
        ("Integer", "fatGap"), ("Integer", "carbohydrateGap"), ("List<String>", "suggestions"),
    ]),
    ("nutrition/vo", "RecipeNutritionAnalysisVO", "vo", [
        ("Long", "recipeId"), ("BigDecimal", "calorie"), ("BigDecimal", "protein"),
        ("BigDecimal", "fat"), ("BigDecimal", "carbohydrate"), ("MacroRatioVO", "macroRatio"),
    ]),
    # risk
    ("risk/dto", "NutritionRiskRuleSaveRequest", "dto", [
        ("String", "ruleCode"), ("String", "ruleName"), ("String", "scenario"), ("String", "nutrient"),
        ("String", "operator"), ("BigDecimal", "thresholdMin"), ("BigDecimal", "thresholdMax"),
        ("String", "severity"), ("String", "message"), ("String", "status"),
    ]),
    ("risk/dto", "RiskEvaluateRequest", "dto", [
        ("String", "sourceType"), ("Long", "sourceId"), ("LocalDate", "date"),
    ]),
    ("risk/dto", "RiskRuleStatusRequest", "dto", [
        ("String", "status", "@NotBlank"),
    ]),
    ("risk/vo", "NutritionRiskRuleVO", "vo", [
        ("Long", "id"), ("String", "ruleCode"), ("String", "ruleName"), ("String", "scenario"),
        ("String", "nutrient"), ("String", "operator"), ("BigDecimal", "thresholdMin"),
        ("BigDecimal", "thresholdMax"), ("String", "severity"), ("String", "message"), ("String", "status"),
    ]),
    ("risk/vo", "NutritionRiskResultVO", "vo", [
        ("Long", "id"), ("LocalDate", "riskDate"), ("String", "severity"), ("String", "sourceType"),
        ("Long", "sourceId"), ("String", "message"), ("String", "suggestion"),
    ]),
    # report
    ("report/dto", "WeeklyReportRequest", "dto", [
        ("LocalDate", "startDate", "@NotNull"), ("LocalDate", "endDate", "@NotNull"),
    ]),
    ("report/dto", "MonthlyReportRequest", "dto", [
        ("String", "month", "@NotBlank"),
    ]),
    ("report/vo", "NutritionReportVO", "vo", [
        ("Long", "id"), ("String", "reportType"), ("LocalDate", "startDate"), ("LocalDate", "endDate"),
        ("BigDecimal", "avgCalorie"), ("BigDecimal", "avgProtein"), ("BigDecimal", "avgFat"),
        ("BigDecimal", "avgCarbohydrate"), ("Integer", "targetDays"), ("Integer", "recordDays"),
        ("BigDecimal", "completionRate"), ("Integer", "riskCount"), ("String", "summary"),
        ("List<String>", "suggestions"), ("List<DailyNutritionPointVO>", "trend"),
    ]),
    # airecipe
    ("airecipe/dto", "AiRecipeParseRequest", "dto", [
        ("String", "prompt"),
    ]),
    ("airecipe/dto", "AiRecipeToMealRecordRequest", "dto", [
        ("LocalDate", "recordDate"), ("String", "mealType"), ("BigDecimal", "servingRatio"),
    ]),
    ("airecipe/vo", "AiRecipeVO", "vo", [
        ("Long", "id"), ("String", "recipeName"), ("String", "description"), ("Integer", "suitabilityScore"),
        ("String", "suitabilityReason"), ("NutritionEstimateVO", "nutritionEstimate"),
        ("List<AiIngredientVO>", "ingredients"), ("List<String>", "cookingSteps"),
        ("List<String>", "warnings"), ("List<RecognizedFoodVO>", "recognizedFoods"),
        ("String", "sourceImageUrl"), ("String", "status"),
    ]),
    ("airecipe/vo", "AiRecipeListItemVO", "vo", [
        ("Long", "id"), ("String", "recipeName"), ("String", "sourceType"), ("String", "status"),
        ("Integer", "suitabilityScore"), ("BigDecimal", "totalCalorie"), ("OffsetDateTime", "createdAt"),
    ]),
    ("airecipe/vo", "AiIngredientVO", "vo", [
        ("String", "name"), ("BigDecimal", "amount"), ("String", "unit"),
    ]),
    ("airecipe/vo", "NutritionEstimateVO", "vo", [
        ("BigDecimal", "calories"), ("BigDecimal", "protein"), ("BigDecimal", "fat"), ("BigDecimal", "carbohydrate"),
    ]),
    ("airecipe/vo", "RecognizedFoodVO", "vo", [
        ("String", "name"), ("BigDecimal", "confidence"),
    ]),
    # community
    ("community/dto", "CommunityPostCreateRequest", "dto", [
        ("String", "title", "@NotBlank"), ("String", "content"), ("String", "recipeName"),
        ("List<String>", "tags"), ("List<Long>", "imageIds", "@NotEmpty"),
    ]),
    ("community/dto", "CommunityPostFromAiRequest", "dto", [
        ("String", "title"), ("String", "content"), ("Boolean", "useAiSourceImage"), ("List<Long>", "imageIds"),
    ]),
    ("community/dto", "CommunityCommentCreateRequest", "dto", [
        ("String", "content", "@NotBlank"),
    ]),
    ("community/vo", "PostImageUploadVO", "vo", [
        ("Long", "imageId"), ("String", "imageUrl"), ("Integer", "width"), ("Integer", "height"),
        ("Long", "fileSize"), ("String", "status"),
    ]),
    ("community/vo", "CommunityPostListItemVO", "vo", [
        ("Long", "id"), ("String", "title"), ("String", "content"), ("String", "authorName"),
        ("String", "recipeName"), ("String", "status"), ("String", "coverImageUrl"), ("Integer", "imageCount"),
        ("List<String>", "tags"), ("NutritionVO", "nutrition"), ("Integer", "likeCount"),
        ("Integer", "favoriteCount"), ("Integer", "commentCount"), ("Boolean", "liked"), ("Boolean", "favorite"),
        ("String", "createdAt"), ("String", "publishedAt"),
    ]),
    ("community/vo", "CommunityPostDetailVO", "vo", [
        ("Long", "id"), ("String", "title"), ("String", "content"), ("String", "authorName"),
        ("String", "recipeName"), ("String", "status"), ("String", "coverImageUrl"), ("Integer", "imageCount"),
        ("List<CommunityPostImageVO>", "images"), ("List<String>", "tags"), ("NutritionVO", "nutrition"),
        ("List<CommunityCommentVO>", "comments"), ("Integer", "likeCount"), ("Integer", "favoriteCount"),
        ("Integer", "commentCount"), ("Boolean", "liked"), ("Boolean", "favorite"),
        ("String", "createdAt"), ("String", "publishedAt"),
    ]),
    ("community/vo", "CommunityPostImageVO", "vo", [
        ("Long", "id"), ("String", "url"), ("Integer", "sortNo"), ("Integer", "width"), ("Integer", "height"),
    ]),
    ("community/vo", "CommunityCommentVO", "vo", [
        ("Long", "id"), ("Long", "postId"), ("String", "authorName"), ("String", "content"), ("String", "createdAt"),
    ]),
    # admin
    ("admin/dto", "ReviewRequest", "dto", [
        ("Boolean", "approved"), ("String", "reason"),
    ]),
    ("admin/vo", "UserAdminVO", "vo", [
        ("Long", "id"), ("String", "username"), ("String", "email"), ("String", "role"),
        ("String", "status"), ("OffsetDateTime", "createdAt"),
    ]),
    ("admin/vo", "AiCallLogVO", "vo", [
        ("Long", "id"), ("Long", "userId"), ("String", "scene"), ("String", "requestSummary"),
        ("Integer", "elapsedMs"), ("Boolean", "success"), ("String", "errorMessage"), ("OffsetDateTime", "createdAt"),
    ]),
    ("admin/vo", "NutritionReportStatisticsVO", "vo", [
        ("Integer", "weeklyReportCount"), ("Integer", "monthlyReportCount"),
        ("Integer", "activeUserCount"), ("BigDecimal", "avgCompletionRate"),
    ]),
    ("admin/vo", "DemoDataResetVO", "vo", [
        ("Boolean", "success"), ("String", "message"),
    ]),
]

TYPE_IMPORTS = {
    "BigDecimal": "java.math.BigDecimal",
    "LocalDate": "java.time.LocalDate",
    "LocalDateTime": "java.time.LocalDateTime",
    "OffsetDateTime": "java.time.OffsetDateTime",
    "List": "java.util.List",
}

def resolve_imports(fields, pkg):
    imports = set()
    validation = False
    for f in fields:
        jtype = f[0]
        if jtype.startswith("List"):
            imports.add("java.util.List")
            inner = jtype[5:-1]
            if inner in TYPE_IMPORTS:
                imports.add(TYPE_IMPORTS[inner])
            elif inner not in ("String", "Long", "Integer", "Boolean"):
                # same package or cross-package - add simple heuristic
                module = pkg.split('.')[2] if len(pkg.split('.')) > 2 else ''
                cross = {
                    "UserVO": "com.example.indras.auth.vo.UserVO",
                    "NutritionVO": "com.example.indras.common.vo.NutritionVO",
                    "MacroRatioVO": "com.example.indras.common.vo.MacroRatioVO",
                    "DailyNutritionPointVO": "com.example.indras.nutrition.vo.DailyNutritionPointVO",
                }
                if inner in cross:
                    imports.add(cross[inner])
                elif inner.endswith("VO") or inner.endswith("DTO"):
                    pass  # same module often
        elif jtype in TYPE_IMPORTS:
            imports.add(TYPE_IMPORTS[jtype])
        if len(f) > 2 and f[2]:
            validation = True
    return imports, validation

def cross_import(jtype, current_pkg):
    mapping = {
        "UserVO": "com.example.indras.auth.vo.UserVO",
        "NutritionVO": "com.example.indras.common.vo.NutritionVO",
        "MacroRatioVO": "com.example.indras.common.vo.MacroRatioVO",
        "DailyNutritionPointVO": "com.example.indras.nutrition.vo.DailyNutritionPointVO",
        "RecipeStepDTO": "com.example.indras.recipe.dto.RecipeStepDTO",
        "RecipeIngredientItemDTO": "com.example.indras.recipe.dto.RecipeIngredientItemDTO",
        "MealRecordItemDTO": "com.example.indras.mealrecord.dto.MealRecordItemDTO",
        "WeightTrendPointVO": "com.example.indras.healthgoal.vo.WeightTrendPointVO",
        "RiskResultBriefVO": "com.example.indras.mealplan.vo.RiskResultBriefVO",
        "MealPlanItemVO": "com.example.indras.mealplan.vo.MealPlanItemVO",
        "RecipeIngredientVO": "com.example.indras.recipe.vo.RecipeIngredientVO",
        "RecipeStepVO": "com.example.indras.recipe.vo.RecipeStepVO",
        "MealRecordItemVO": "com.example.indras.mealrecord.vo.MealRecordItemVO",
        "MealRecordSummaryVO": "com.example.indras.mealrecord.vo.MealRecordSummaryVO",
        "CommunityPostImageVO": "com.example.indras.community.vo.CommunityPostImageVO",
        "CommunityCommentVO": "com.example.indras.community.vo.CommunityCommentVO",
        "NutritionEstimateVO": "com.example.indras.airecipe.vo.NutritionEstimateVO",
        "AiIngredientVO": "com.example.indras.airecipe.vo.AiIngredientVO",
        "RecognizedFoodVO": "com.example.indras.airecipe.vo.RecognizedFoodVO",
    }
    return mapping.get(jtype)

for pkg_suffix, cls, kind, fields in CLASSES:
    pkg = f"com.example.indras.{pkg_suffix.replace('/', '.')}"
    imports = set()
    use_validation = False
    field_lines = []
    for f in fields:
        jtype, name = f[0], f[1]
        ann = f[2] if len(f) > 2 else None
        if ann:
            use_validation = True
            for part in ann.replace("@NotBlank", "@jakarta.validation.constraints.NotBlank") \
                    .replace("@NotNull", "@jakarta.validation.constraints.NotNull") \
                    .replace("@NotEmpty", "@jakarta.validation.constraints.NotEmpty") \
                    .replace("@Email", "@jakarta.validation.constraints.Email") \
                    .replace("@Size", "@jakarta.validation.constraints.Size").split():
                if part.startswith("@jakarta"):
                    field_lines.append(f"    {part}")
        if jtype in TYPE_IMPORTS:
            imports.add(TYPE_IMPORTS[jtype])
        if jtype.startswith("List"):
            imports.add("java.util.List")
        ci = cross_import(jtype, pkg)
        if ci:
            imports.add(ci)
        elif "<" in jtype:
            inner = jtype[jtype.index("<")+1:jtype.index(">")]
            ci2 = cross_import(inner, pkg)
            if ci2:
                imports.add(ci2)
        field_lines.append(f"    private {jtype} {name};")

    val_import = "import jakarta.validation.constraints.*;\n" if use_validation else ""
    import_block = "\n".join(f"import {i};" for i in sorted(imports))
    content = f"""package {pkg};

import lombok.*;
{val_import}{import_block}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {cls} {{

{chr(10).join(field_lines)}
}}
"""
    path = BASE / pkg_suffix / f"{cls}.java"
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")
    print(cls)

print(f"Total DTO/VO: {len(CLASSES)}")
