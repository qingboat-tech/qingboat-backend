package com.qingboat.ts.service;

import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.entity.GoodsSkuEntity;

import java.util.List;

public interface GoodsService {

    GoodsEntity getGoodsById(Long goodsId);


    List<GoodsSkuEntity> getGoodsSkuByGoodsId(Long goodsId);

    GoodsSkuEntity getGoodsSkuById(Long goodsSkuId);

}
