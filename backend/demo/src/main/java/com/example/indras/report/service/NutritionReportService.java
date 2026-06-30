package com.example.indras.report.service;

import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.report.dto.MonthlyReportRequest;
import com.example.indras.report.dto.WeeklyReportRequest;
import com.example.indras.report.vo.NutritionReportVO;

public interface NutritionReportService {

    NutritionReportVO generateWeekly(Long userId, WeeklyReportRequest request);

    NutritionReportVO generateMonthly(Long userId, MonthlyReportRequest request);

    PageResult<NutritionReportVO> page(Long userId, PageQuery query);

    NutritionReportVO detail(Long userId, Long id);

    boolean delete(Long userId, Long id);
}
