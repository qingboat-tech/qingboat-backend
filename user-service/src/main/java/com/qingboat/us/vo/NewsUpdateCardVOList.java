package com.qingboat.us.vo;

import java.util.List;

public class NewsUpdateCardVOList {
    private List<NewsUpdateCardVO> newsUpdateCardVOList;
    private Integer total;


    public List<NewsUpdateCardVO> getNewsUpdateCardVOList() {
        return newsUpdateCardVOList;
    }

    public void setNewsUpdateCardVOList(List<NewsUpdateCardVO> newsUpdateCardVOList) {
        this.newsUpdateCardVOList = newsUpdateCardVOList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
