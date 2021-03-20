package com.qingboat.ts.vo;

import com.qingboat.ts.entity.GoodsEntity;
import lombok.Data;

import java.util.List;

@Data
public class GoodsVo extends GoodsEntity {

    private List skuList;
}
