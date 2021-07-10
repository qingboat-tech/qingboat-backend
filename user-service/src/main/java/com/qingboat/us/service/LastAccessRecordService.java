package com.qingboat.us.service;

import com.qingboat.base.api.ApiResponse;

public interface LastAccessRecordService {
    public ApiResponse lastAccessRecord(Integer userId,Integer type,String targetId);
}
