package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.qingboat.ts.dao.GoodsDao;
import com.qingboat.ts.dao.GoodsSkuDao;
import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.entity.GoodsSkuEntity;
import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.GoodsService;
import com.qingboat.ts.vo.GoodsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsSkuDao goodsSkuDao;


    @Override
    public GoodsEntity getGoodsById(Long goodsId) {
        GoodsEntity goodsEntity = goodsDao.selectById(goodsId);
        if (goodsEntity!=null && goodsEntity.getHasSku()){
            Wrapper<GoodsSkuEntity> param = null;
            List list= goodsSkuDao.selectList (param);
            goodsEntity.setSkuList(list);
        }
        return goodsEntity;
    }
}
