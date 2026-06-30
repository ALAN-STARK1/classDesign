package com.example.indras.nutrition.service;

import com.example.indras.nutrition.vo.NutritionGapVO;
import com.example.indras.nutrition.vo.NutritionRangeVO;
import com.example.indras.nutrition.vo.TodayNutritionVO;
import com.example.indras.risk.vo.NutritionRiskResultVO;

import java.time.LocalDate;
import java.util.List;

public interface NutritionAnalysisService {

    TodayNutritionVO today(Long userId, LocalDate date);

    NutritionRangeVO range(Long userId, LocalDate startDate, LocalDate endDate);

    NutritionGapVO gap(Long userId, LocalDate date);

    List<NutritionRiskResultVO> risks(Long userId, LocalDate startDate, LocalDate endDate);
}
