package com.qingboat.ts.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("apps_goods")// 映射数据库表名
public class GoodsEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    @TableField("`desc`")
    private String desc;

    private String imgUrl;

    private String productType;

    private Long productId;

    private Integer price;

    private String currency;

    private Integer inventory;

    private Date createdAt;

    private Date updatedAt;

    private Boolean hasSku;

    @TableField(exist = false)
    private List<GoodsSkuEntity> skuList;


}
