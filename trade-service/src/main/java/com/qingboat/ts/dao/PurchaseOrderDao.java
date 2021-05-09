package com.qingboat.ts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.ts.entity.PurchaseOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface PurchaseOrderDao extends BaseMapper<PurchaseOrderEntity> {

}

