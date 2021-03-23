package com.qingboat.ts.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("apps_goods_sku")// 映射数据库表名
public class GoodsSkuEntity {

    @TableId(type = IdType.AUTO)
    private Long id;


    private String name;

    private Integer price;

    private Integer inventory; //库存数

    private Long goodsId;

    @TableField(select = false)
    private Date createdAt;

    @TableField(select = false)
    private Date updatedAt;


}
