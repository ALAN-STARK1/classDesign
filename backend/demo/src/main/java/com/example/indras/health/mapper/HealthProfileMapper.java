package com.example.indras.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.indras.health.entity.HealthProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthProfileMapper extends BaseMapper<HealthProfile> {
}
