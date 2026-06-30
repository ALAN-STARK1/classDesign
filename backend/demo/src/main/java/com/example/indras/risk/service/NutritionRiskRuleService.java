package com.example.indras.risk.service;

import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.risk.dto.NutritionRiskRuleSaveRequest;
import com.example.indras.risk.dto.RiskEvaluateRequest;
import com.example.indras.risk.dto.RiskRuleStatusRequest;
import com.example.indras.risk.vo.NutritionRiskResultVO;
import com.example.indras.risk.vo.NutritionRiskRuleVO;

import java.util.List;

public interface NutritionRiskRuleService {

    PageResult<NutritionRiskRuleVO> page(PageQuery query, String scenario);

    NutritionRiskRuleVO create(NutritionRiskRuleSaveRequest request);

    NutritionRiskRuleVO update(Long id, NutritionRiskRuleSaveRequest request);

    NutritionRiskRuleVO updateStatus(Long id, RiskRuleStatusRequest request);

    List<NutritionRiskResultVO> evaluate(Long userId, RiskEvaluateRequest request);
}
