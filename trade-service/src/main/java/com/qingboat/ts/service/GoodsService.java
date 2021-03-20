package com.qingboat.ts.service;

import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.vo.GoodsVo;

public interface GoodsService {

    GoodsVo getGoodsById(Long goodsId);

}
