package com.example.indras.admin.service;

import com.example.indras.admin.dto.ReviewRequest;
import com.example.indras.admin.vo.AiCallLogVO;
import com.example.indras.admin.vo.DemoDataResetVO;
import com.example.indras.admin.vo.NutritionReportStatisticsVO;
import com.example.indras.admin.vo.UserAdminVO;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.community.vo.CommunityPostDetailVO;
import com.example.indras.recipe.vo.RecipeDetailVO;
import com.example.indras.risk.vo.NutritionRiskResultVO;

public interface AdminService {

    PageResult<UserAdminVO> pageUsers(PageQuery query);

    UserAdminVO disableUser(Long id);

    RecipeDetailVO reviewRecipe(Long id, ReviewRequest request);

    CommunityPostDetailVO reviewPost(Long id, ReviewRequest request);

    PageResult<AiCallLogVO> pageAiCallLogs(PageQuery query);

    PageResult<NutritionRiskResultVO> pageRiskResults(PageQuery query, String severity);

    NutritionReportStatisticsVO reportStatistics();

    DemoDataResetVO resetDemoData();
}
