package com.qingboat.as.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.BenefitEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface BenefitDao extends BaseMapper<BenefitEntity> {

}