package com.qingboat.as.service.impl;

import com.qingboat.as.api.WxService;
import com.qingboat.as.dao.ArticleMongoDao;
import com.qingboat.as.filter.AuthFilter;
import com.qingboat.as.service.WxMessageService;
import org.springframework.beans.factory.annotation.Autowired;

public class WxMessageServiceImpl implements WxMessageService {


    @Autowired
    private WxService wxService;

    @Override
    public Object getToken(String userId) {
        String sec = AuthFilter.getSecret(userId);
        Object token = wxService.getWxUserToken(sec,userId);
        return token == null?null:token.toString();
    }



}
