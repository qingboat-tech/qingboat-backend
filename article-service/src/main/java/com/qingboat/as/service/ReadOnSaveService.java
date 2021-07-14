package com.qingboat.as.service;

import com.qingboat.base.api.ApiResponse;

public interface ReadOnSaveService {

    public ApiResponse readOnSave(Integer userId,Integer contentType,String contentId,Integer height);
}
