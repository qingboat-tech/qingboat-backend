package com.qingboat.as.service;

import com.qingboat.as.vo.ReadOnListVo;
import com.qingboat.base.api.ApiResponse;

public interface ReadOnSaveService {

    public ApiResponse readOnSave(Integer userId,Integer contentType,String contentId,Integer height,Integer pathwayId);

    public ReadOnListVo readOnList(Integer userId,Integer page,Integer pageSize);

}
