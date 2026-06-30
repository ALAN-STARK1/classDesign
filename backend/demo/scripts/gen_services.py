from pathlib import Path

BASE = Path(__file__).resolve().parents[1] / "src/main/java/com/example/indras"

services = [
    ("healthgoal", "HealthGoalCycleService", "HealthGoalCycleServiceImpl", [
        "HealthGoalCycleVO create(Long userId, HealthGoalCycleCreateRequest request);",
        "HealthGoalCycleVO getCurrent(Long userId);",
        "com.example.indras.common.api.PageResult<HealthGoalCycleVO> page(Long userId, com.example.indras.common.api.PageQuery query);",
        "HealthGoalCycleVO update(Long userId, Long id, HealthGoalCycleUpdateRequest request);",
        "HealthGoalCompleteVO complete(Long userId, Long id);",
        "HealthGoalCycleVO cancel(Long userId, Long id);",
        "HealthGoalProgressVO progress(Long userId, Long id);",
    ], ["healthgoal.dto", "healthgoal.vo", "common.api"]),
    ("mealrecord", "MealRecordService", "MealRecordServiceImpl", [
        "MealRecordVO createManual(Long userId, MealRecordCreateRequest request);",
        "MealRecordVO fromRecipe(Long userId, Long recipeId, MealRecordFromSourceRequest request);",
        "DailyMealRecordVO getDay(Long userId, java.time.LocalDate date);",
        "boolean delete(Long userId, Long id);",
    ], ["mealrecord.dto", "mealrecord.vo"]),
    ("nutrition", "NutritionAnalysisService", "NutritionAnalysisServiceImpl", [
        "TodayNutritionVO today(Long userId);",
        "NutritionRangeVO range(Long userId, java.time.LocalDate start, java.time.LocalDate end);",
        "NutritionGapVO gap(Long userId, java.time.LocalDate date);",
        "java.util.List<com.example.indras.risk.vo.NutritionRiskResultVO> risks(Long userId, java.time.LocalDate start, java.time.LocalDate end);",
    ], ["nutrition.vo", "risk.vo"]),
    ("risk", "NutritionRiskRuleService", "NutritionRiskRuleServiceImpl", [
        "com.example.indras.common.api.PageResult<NutritionRiskRuleVO> page(com.example.indras.common.api.PageQuery query, String scenario);",
        "NutritionRiskRuleVO create(NutritionRiskRuleSaveRequest request);",
        "NutritionRiskRuleVO update(Long id, NutritionRiskRuleSaveRequest request);",
        "NutritionRiskRuleVO updateStatus(Long id, RiskRuleStatusRequest request);",
        "java.util.List<NutritionRiskResultVO> evaluate(Long userId, RiskEvaluateRequest request);",
    ], ["risk.dto", "risk.vo", "common.api"]),
    ("report", "NutritionReportService", "NutritionReportServiceImpl", [
        "NutritionReportVO weekly(Long userId, WeeklyReportRequest request);",
        "NutritionReportVO monthly(Long userId, MonthlyReportRequest request);",
        "com.example.indras.common.api.PageResult<NutritionReportVO> page(Long userId, com.example.indras.common.api.PageQuery query, String reportType);",
        "NutritionReportVO detail(Long userId, Long id);",
        "boolean delete(Long userId, Long id);",
    ], ["report.dto", "report.vo", "common.api"]),
    ("airecipe", "AiRecipeService", "AiRecipeServiceImpl", [
        "AiRecipeVO parseText(Long userId, AiRecipeParseRequest request);",
        "AiRecipeVO getById(Long userId, Long id);",
        "com.example.indras.common.api.PageResult<AiRecipeListItemVO> history(Long userId, com.example.indras.common.api.PageQuery query);",
        "AiRecipeVO confirm(Long userId, Long id);",
        "boolean delete(Long userId, Long id);",
    ], ["airecipe.dto", "airecipe.vo", "common.api"]),
    ("community", "CommunityService", "CommunityServiceImpl", [
        "com.example.indras.common.api.PageResult<CommunityPostListItemVO> page(com.example.indras.common.api.PageQuery query, String status);",
        "CommunityPostDetailVO detail(Long postId);",
        "CommunityCommentVO comment(Long userId, Long postId, CommunityCommentCreateRequest request);",
    ], ["community.dto", "community.vo", "common.api"]),
    ("admin", "AdminService", "AdminServiceImpl", [
        "com.example.indras.common.api.PageResult<UserAdminVO> pageUsers(com.example.indras.common.api.PageQuery query);",
        "UserAdminVO disableUser(Long userId);",
        "com.example.indras.common.api.PageResult<AiCallLogVO> aiCallLogs(com.example.indras.common.api.PageQuery query);",
        "NutritionReportStatisticsVO reportStatistics();",
    ], ["admin.vo", "common.api"]),
]

for module, iface, impl, methods, imports in services:
    pkg = f"com.example.indras.{module}"
    import_lines = []
    for imp in imports:
        import_lines.append(f"import {pkg if imp.startswith(module) else 'com.example.indras.' + imp.replace('.', '.')}.*;")
    # fix imports manually in template
    iface_content = f"""package {pkg}.service;

import {pkg}.service.impl.*;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;

public interface {iface} {{

{chr(10).join('    ' + m for m in methods)}
}}
"""
    # simpler - skip auto gen impl stubs, user has manual impl for key ones
    iface_path = BASE / module / "service" / f"{iface}.java"
    iface_path.parent.mkdir(parents=True, exist_ok=True)
    print(f"Would generate {iface_path.name}")
