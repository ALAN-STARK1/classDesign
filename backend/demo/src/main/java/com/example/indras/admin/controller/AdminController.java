package com.example.indras.admin.controller;

import com.example.indras.admin.dto.ReviewRequest;
import com.example.indras.admin.service.AdminService;
import com.example.indras.admin.vo.AiCallLogVO;
import com.example.indras.admin.vo.DemoDataResetVO;
import com.example.indras.admin.vo.NutritionReportStatisticsVO;
import com.example.indras.admin.vo.UserAdminVO;
import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.community.vo.CommunityPostDetailVO;
import com.example.indras.recipe.vo.RecipeDetailVO;
import com.example.indras.risk.vo.NutritionRiskResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ApiResponse<PageResult<UserAdminVO>> pageUsers(PageQuery query) {
        return ApiResponse.success(adminService.pageUsers(query));
    }

    @PatchMapping("/users/{id}/disable")
    public ApiResponse<UserAdminVO> disableUser(@PathVariable Long id) {
        return ApiResponse.success(adminService.disableUser(id));
    }

    @PatchMapping("/recipes/{id}/review")
    public ApiResponse<RecipeDetailVO> reviewRecipe(@PathVariable Long id,
                                                    @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(adminService.reviewRecipe(id, request));
    }

    @PatchMapping("/community/posts/{id}/review")
    public ApiResponse<CommunityPostDetailVO> reviewPost(@PathVariable Long id,
                                                         @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(adminService.reviewPost(id, request));
    }

    @DeleteMapping("/community/posts/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/ai-call-logs")
    public ApiResponse<PageResult<AiCallLogVO>> pageAiCallLogs(PageQuery query) {
        return ApiResponse.success(adminService.pageAiCallLogs(query));
    }

    @GetMapping("/nutrition-risk-results")
    public ApiResponse<PageResult<NutritionRiskResultVO>> pageRiskResults(PageQuery query,
                                                                          @RequestParam(required = false) String severity) {
        return ApiResponse.success(adminService.pageRiskResults(query, severity));
    }

    @GetMapping("/nutrition-reports/statistics")
    public ApiResponse<NutritionReportStatisticsVO> reportStatistics() {
        return ApiResponse.success(adminService.reportStatistics());
    }

    @PostMapping("/demo-data/reset")
    public ApiResponse<DemoDataResetVO> resetDemoData() {
        return ApiResponse.success(adminService.resetDemoData());
    }
}
