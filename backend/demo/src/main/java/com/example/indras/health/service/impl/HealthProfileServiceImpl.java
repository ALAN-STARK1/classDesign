package com.example.indras.health.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.indras.common.enums.ActivityLevel;
import com.example.indras.common.enums.Gender;
import com.example.indras.common.enums.HealthGoal;
import com.example.indras.common.util.HealthCalculator;
import com.example.indras.health.dto.AllergensUpdateRequest;
import com.example.indras.health.dto.HealthProfileSaveRequest;
import com.example.indras.health.dto.RestrictionsUpdateRequest;
import com.example.indras.health.entity.HealthProfile;
import com.example.indras.health.entity.UserAllergen;
import com.example.indras.health.entity.UserDietRestriction;
import com.example.indras.health.mapper.HealthProfileMapper;
import com.example.indras.health.mapper.UserAllergenMapper;
import com.example.indras.health.mapper.UserDietRestrictionMapper;
import com.example.indras.health.service.HealthProfileService;
import com.example.indras.health.vo.HealthProfileSummaryVO;
import com.example.indras.health.vo.HealthProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthProfileServiceImpl implements HealthProfileService {

    private final HealthProfileMapper healthProfileMapper;
    private final UserAllergenMapper userAllergenMapper;
    private final UserDietRestrictionMapper userDietRestrictionMapper;

    @Override
    public HealthProfileVO getMyProfile(Long userId) {
        HealthProfile profile = healthProfileMapper.selectOne(Wrappers.<HealthProfile>lambdaQuery()
                .eq(HealthProfile::getUserId, userId));
        if (profile == null) {
            return HealthProfileVO.builder()
                    .allergens(listAllergens(userId))
                    .restrictions(listRestrictions(userId))
                    .build();
        }
        return toVo(profile, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthProfileVO saveMyProfile(Long userId, HealthProfileSaveRequest request) {
        Gender gender = HealthCalculator.parseGender(request.getGender());
        ActivityLevel activityLevel = HealthCalculator.parseActivityLevel(request.getActivityLevel());
        HealthGoal healthGoal = HealthCalculator.parseHealthGoal(request.getHealthGoal());

        BigDecimal bmi = HealthCalculator.calculateBmi(request.getHeightCm(), request.getWeightKg());
        int bmr = HealthCalculator.calculateBmr(gender, request.getHeightCm(), request.getWeightKg(), request.getBirthday());
        int tdee = HealthCalculator.calculateTdee(bmr, activityLevel);
        int dailyTarget = HealthCalculator.calculateDailyCalorieTarget(tdee, healthGoal);

        HealthProfile profile = healthProfileMapper.selectOne(Wrappers.<HealthProfile>lambdaQuery()
                .eq(HealthProfile::getUserId, userId));
        if (profile == null) {
            profile = HealthProfile.builder().userId(userId).build();
        }
        profile.setGender(request.getGender());
        profile.setBirthday(request.getBirthday());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setTargetWeightKg(request.getTargetWeightKg());
        profile.setActivityLevel(request.getActivityLevel());
        profile.setHealthGoal(request.getHealthGoal());
        profile.setBmi(bmi);
        profile.setDailyCalorieTarget(dailyTarget);

        if (profile.getId() == null) {
            healthProfileMapper.insert(profile);
        } else {
            healthProfileMapper.updateById(profile);
        }
        return toVo(profile, userId);
    }

    @Override
    public HealthProfileSummaryVO getMySummary(Long userId) {
        HealthProfile profile = healthProfileMapper.selectOne(Wrappers.<HealthProfile>lambdaQuery()
                .eq(HealthProfile::getUserId, userId));
        if (profile == null) {
            return HealthProfileSummaryVO.builder().build();
        }
        Gender gender = HealthCalculator.parseGender(profile.getGender());
        ActivityLevel activityLevel = HealthCalculator.parseActivityLevel(profile.getActivityLevel());
        HealthGoal healthGoal = HealthCalculator.parseHealthGoal(profile.getHealthGoal());
        int bmr = HealthCalculator.calculateBmr(gender, profile.getHeightCm(), profile.getWeightKg(), profile.getBirthday());
        int tdee = HealthCalculator.calculateTdee(bmr, activityLevel);
        return HealthProfileSummaryVO.builder()
                .bmi(profile.getBmi())
                .bmiStatus(HealthCalculator.bmiStatus(profile.getBmi()))
                .bmr(bmr)
                .tdee(tdee)
                .dailyCalorieTarget(profile.getDailyCalorieTarget())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> updateAllergens(Long userId, AllergensUpdateRequest request) {
        userAllergenMapper.delete(Wrappers.<UserAllergen>lambdaQuery().eq(UserAllergen::getUserId, userId));
        for (String allergen : request.getAllergens()) {
            userAllergenMapper.insert(UserAllergen.builder().userId(userId).allergenName(allergen).build());
        }
        return request.getAllergens();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> updateRestrictions(Long userId, RestrictionsUpdateRequest request) {
        userDietRestrictionMapper.delete(Wrappers.<UserDietRestriction>lambdaQuery().eq(UserDietRestriction::getUserId, userId));
        for (String restriction : request.getRestrictions()) {
            userDietRestrictionMapper.insert(UserDietRestriction.builder().userId(userId).restrictionName(restriction).build());
        }
        return request.getRestrictions();
    }

    private HealthProfileVO toVo(HealthProfile profile, Long userId) {
        return HealthProfileVO.builder()
                .id(profile.getId())
                .gender(profile.getGender())
                .birthday(profile.getBirthday())
                .heightCm(profile.getHeightCm())
                .weightKg(profile.getWeightKg())
                .targetWeightKg(profile.getTargetWeightKg())
                .activityLevel(profile.getActivityLevel())
                .healthGoal(profile.getHealthGoal())
                .bmi(profile.getBmi())
                .dailyCalorieTarget(profile.getDailyCalorieTarget())
                .allergens(listAllergens(userId))
                .restrictions(listRestrictions(userId))
                .build();
    }

    private List<String> listAllergens(Long userId) {
        return userAllergenMapper.selectList(Wrappers.<UserAllergen>lambdaQuery().eq(UserAllergen::getUserId, userId))
                .stream().map(UserAllergen::getAllergenName).toList();
    }

    private List<String> listRestrictions(Long userId) {
        return userDietRestrictionMapper.selectList(Wrappers.<UserDietRestriction>lambdaQuery().eq(UserDietRestriction::getUserId, userId))
                .stream().map(UserDietRestriction::getRestrictionName).toList();
    }
}
