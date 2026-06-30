package com.example.indras.healthgoal.service;

import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.healthgoal.dto.HealthGoalCycleCreateRequest;
import com.example.indras.healthgoal.dto.HealthGoalCycleUpdateRequest;
import com.example.indras.healthgoal.vo.HealthGoalCompleteVO;
import com.example.indras.healthgoal.vo.HealthGoalCycleVO;
import com.example.indras.healthgoal.vo.HealthGoalProgressVO;

public interface HealthGoalCycleService {

    HealthGoalCycleVO create(Long userId, HealthGoalCycleCreateRequest request);

    HealthGoalCycleVO getCurrent(Long userId);

    PageResult<HealthGoalCycleVO> page(Long userId, PageQuery query);

    HealthGoalCycleVO update(Long userId, Long id, HealthGoalCycleUpdateRequest request);

    HealthGoalCompleteVO complete(Long userId, Long id);

    HealthGoalCycleVO cancel(Long userId, Long id);

    HealthGoalProgressVO progress(Long userId, Long id);
}
