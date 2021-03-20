package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.qingboat.ts.dao.GoodsDao;
import com.qingboat.ts.dao.GoodsSkuDao;
import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.entity.GoodsSkuEntity;
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
    public GoodsVo getGoodsById(Long goodsId) {
        GoodsEntity goodsEntity = goodsDao.selectById(goodsId);
        GoodsVo goodsVo = null;
        if (goodsEntity!=null && goodsEntity.getHasSku()){
            goodsVo = new GoodsVo();
            BeanUtils.copyProperties(goodsEntity,goodsVo );

            Wrapper<GoodsSkuEntity> param = null;
            List list= goodsSkuDao.selectList (param);
            goodsVo.setSkuList(list);
        }
        return goodsVo;
    }
}
