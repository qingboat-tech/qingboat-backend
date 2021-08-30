package com.qingboat.base;
//线程是否安全？？？  有可能会出现线程不安全的情况 一般正常时候时不用出现问题
public enum MessageType {
    //飞书系列
    FS_ARTICLE_APPROVED(1,"文章审核通过"),
    FS_CREATOR_APPLY(2,"创作者申请"),
    FS_OPERATING_DATA(3,"每周运营数据"),

    ;
    private Integer typeId;
    private String typeName;


    MessageType(Integer typeId,String typeName){
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }
}
