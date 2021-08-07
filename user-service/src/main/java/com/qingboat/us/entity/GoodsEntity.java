package com.qingboat.us.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class GoodsEntity {
    @Id
    private Integer id;
    private String title;
    private String desc;
    private String imgUrl;
    private Integer productType;
    private Integer productId;
    private Integer price;
    private Integer inventory;
    private Integer hasSku;
    private String currency;


}
