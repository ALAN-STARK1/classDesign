package com.example.indras.health.service;

import com.example.indras.health.dto.AllergensUpdateRequest;
import com.example.indras.health.dto.HealthProfileSaveRequest;
import com.example.indras.health.dto.RestrictionsUpdateRequest;
import com.example.indras.health.vo.HealthProfileSummaryVO;
import com.example.indras.health.vo.HealthProfileVO;

import java.util.List;

public interface HealthProfileService {

    HealthProfileVO getMyProfile(Long userId);

    HealthProfileVO saveMyProfile(Long userId, HealthProfileSaveRequest request);

    HealthProfileSummaryVO getMySummary(Long userId);

    List<String> updateAllergens(Long userId, AllergensUpdateRequest request);

    List<String> updateRestrictions(Long userId, RestrictionsUpdateRequest request);
}
