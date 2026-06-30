package com.example.indras.healthgoal.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.healthgoal.dto.HealthGoalCycleCreateRequest;
import com.example.indras.healthgoal.dto.HealthGoalCycleUpdateRequest;
import com.example.indras.healthgoal.service.HealthGoalCycleService;
import com.example.indras.healthgoal.vo.HealthGoalCompleteVO;
import com.example.indras.healthgoal.vo.HealthGoalCycleVO;
import com.example.indras.healthgoal.vo.HealthGoalProgressVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/health-goal-cycles")
@RequiredArgsConstructor
public class HealthGoalCycleController {

    private final HealthGoalCycleService healthGoalCycleService;

    @PostMapping
    public ApiResponse<HealthGoalCycleVO> create(@Valid @RequestBody HealthGoalCycleCreateRequest request) {
        return ApiResponse.success(healthGoalCycleService.create(UserContext.requireUserId(), request));
    }

    @GetMapping("/current")
    public ApiResponse<HealthGoalCycleVO> current() {
        return ApiResponse.success(healthGoalCycleService.getCurrent(UserContext.requireUserId()));
    }

    @GetMapping
    public ApiResponse<PageResult<HealthGoalCycleVO>> page(PageQuery query) {
        return ApiResponse.success(healthGoalCycleService.page(UserContext.requireUserId(), query));
    }

    @PutMapping("/{id}")
    public ApiResponse<HealthGoalCycleVO> update(@PathVariable Long id,
                                                 @Valid @RequestBody HealthGoalCycleUpdateRequest request) {
        return ApiResponse.success(healthGoalCycleService.update(UserContext.requireUserId(), id, request));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<HealthGoalCompleteVO> complete(@PathVariable Long id) {
        return ApiResponse.success(healthGoalCycleService.complete(UserContext.requireUserId(), id));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<HealthGoalCycleVO> cancel(@PathVariable Long id) {
        return ApiResponse.success(healthGoalCycleService.cancel(UserContext.requireUserId(), id));
    }

    @GetMapping("/{id}/progress")
    public ApiResponse<HealthGoalProgressVO> progress(@PathVariable Long id) {
        return ApiResponse.success(healthGoalCycleService.progress(UserContext.requireUserId(), id));
    }
}
