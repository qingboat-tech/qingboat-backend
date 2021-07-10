package com.qingboat.us.vo;

import java.util.List;

public class TaSubscriptionNewslettersWithTotalVO {
    private List<TaSubscriptionNewslettersVO> list;
    private Integer total;

    public TaSubscriptionNewslettersWithTotalVO() {
    }

    public TaSubscriptionNewslettersWithTotalVO(List<TaSubscriptionNewslettersVO> list, Integer total) {
        this.list = list;
        this.total = total;
    }

    public List<TaSubscriptionNewslettersVO> getList() {
        return list;
    }

    public void setList(List<TaSubscriptionNewslettersVO> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
