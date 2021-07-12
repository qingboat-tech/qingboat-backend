package com.qingboat.us.service;

import com.qingboat.us.vo.NewsUpdateCardVOList;

public interface NewsUpdateService {
    public NewsUpdateCardVOList getNewsUpdateCardVOList(Integer userId,Integer page,Integer pageSize);
}
