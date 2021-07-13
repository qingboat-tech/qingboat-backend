package com.qingboat.us.service;

import com.qingboat.us.vo.NewsUpdateCardVOList;
import com.qingboat.us.vo.UserProfileVO1;

import java.util.List;

public interface NewsUpdateService {
    public NewsUpdateCardVOList getNewsUpdateCardVOList(Integer userId,Integer page,Integer pageSize);

    public List<UserProfileVO1> haveUpdate(Integer userId,List<UserProfileVO1> list);
}
