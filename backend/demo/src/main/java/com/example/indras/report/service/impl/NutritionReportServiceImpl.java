package com.example.indras.report.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.JsonHelper;
import com.example.indras.common.util.PageUtils;
import com.example.indras.nutrition.service.NutritionAnalysisService;
import com.example.indras.nutrition.vo.DailyNutritionPointVO;
import com.example.indras.nutrition.vo.NutritionRangeVO;
import com.example.indras.report.dto.MonthlyReportRequest;
import com.example.indras.report.dto.WeeklyReportRequest;
import com.example.indras.report.entity.NutritionReport;
import com.example.indras.report.mapper.NutritionReportMapper;
import com.example.indras.report.service.NutritionReportService;
import com.example.indras.report.vo.NutritionReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NutritionReportServiceImpl implements NutritionReportService {

    private final NutritionReportMapper nutritionReportMapper;
    private final NutritionAnalysisService nutritionAnalysisService;
    private final JsonHelper jsonHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NutritionReportVO generateWeekly(Long userId, WeeklyReportRequest request) {
        return generate(userId, "WEEKLY", request.getStartDate(), request.getEndDate());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NutritionReportVO generateMonthly(Long userId, MonthlyReportRequest request) {
        java.time.YearMonth yearMonth = java.time.YearMonth.parse(request.getMonth());
        java.time.LocalDate startDate = yearMonth.atDay(1);
        java.time.LocalDate endDate = yearMonth.atEndOfMonth();
        return generate(userId, "MONTHLY", startDate, endDate);
    }

    @Override
    public PageResult<NutritionReportVO> page(Long userId, PageQuery query) {
        Page<NutritionReport> page = nutritionReportMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<NutritionReport>lambdaQuery()
                        .eq(NutritionReport::getUserId, userId)
                        .orderByDesc(NutritionReport::getId));
        return PageUtils.toPageResult(page, this::toVo);
    }

    @Override
    public NutritionReportVO detail(Long userId, Long id) {
        NutritionReport report = requireOwned(userId, id);
        return toVo(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long userId, Long id) {
        NutritionReport report = requireOwned(userId, id);
        return nutritionReportMapper.deleteById(report.getId()) > 0;
    }

    private NutritionReportVO generate(Long userId, String reportType, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        NutritionRangeVO range = nutritionAnalysisService.range(userId, startDate, endDate);
        List<DailyNutritionPointVO> days = range.getDays() == null ? List.of() : range.getDays();
        BigDecimal avgCalorie = average(days.stream().map(DailyNutritionPointVO::getCalorie).toList());
        BigDecimal avgProtein = average(days.stream().map(DailyNutritionPointVO::getProtein).toList());
        BigDecimal avgFat = average(days.stream().map(DailyNutritionPointVO::getFat).toList());
        BigDecimal avgCarb = average(days.stream().map(DailyNutritionPointVO::getCarbohydrate).toList());
        int targetDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int recordDays = days.size();
        BigDecimal completionRate = targetDays == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(recordDays * 100.0 / targetDays).setScale(2, RoundingMode.HALF_UP);
        NutritionReport report = NutritionReport.builder()
                .userId(userId)
                .reportType(reportType)
                .startDate(startDate)
                .endDate(endDate)
                .avgCalorie(avgCalorie)
                .avgProtein(avgProtein)
                .avgFat(avgFat)
                .avgCarbohydrate(avgCarb)
                .targetDays(targetDays)
                .recordDays(recordDays)
                .completionRate(completionRate)
                .riskCount(0)
                .summary("报告已生成，共记录 " + recordDays + " 天。")
                .suggestionsJson(jsonHelper.write(List.of("继续保持记录习惯。", "关注蛋白质与热量平衡。")))
                .createdAt(LocalDateTime.now())
                .build();
        nutritionReportMapper.insert(report);
        NutritionReportVO vo = toVo(report);
        vo.setTrend(days);
        return vo;
    }

    private NutritionReport requireOwned(Long userId, Long id) {
        NutritionReport report = nutritionReportMapper.selectById(id);
        if (report == null || !Objects.equals(report.getUserId(), userId)) {
            throw BizException.notFound("营养报告不存在");
        }
        return report;
    }

    private NutritionReportVO toVo(NutritionReport report) {
        return NutritionReportVO.builder()
                .id(report.getId())
                .reportType(report.getReportType())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .avgCalorie(report.getAvgCalorie())
                .avgProtein(report.getAvgProtein())
                .avgFat(report.getAvgFat())
                .avgCarbohydrate(report.getAvgCarbohydrate())
                .targetDays(report.getTargetDays())
                .recordDays(report.getRecordDays())
                .completionRate(report.getCompletionRate())
                .riskCount(report.getRiskCount())
                .summary(report.getSummary())
                .suggestions(jsonHelper.readStringList(report.getSuggestionsJson()))
                .build();
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = values.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }
}
