package com.qingboat.as.vo;

import java.util.List;

public class ReadOnListVo {
    private List<ReadOnVo> list;
    private Integer total;

    public List<ReadOnVo> getList() {
        return list;
    }

    public void setList(List<ReadOnVo> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
