package com.qingboat.as.controller;

import com.qingboat.as.service.ReadOnSaveService;
import com.qingboat.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class ReadOnController extends BaseController {


    @Autowired
    ReadOnSaveService readOnSaveService;
    //使用redis缓存
    @PostMapping("/readOnSave")
    public ApiResponse readOnSave(@RequestBody Map<String,Object> param){
        Integer userId = getUId().intValue();
        Integer contentType = Integer.parseInt(param.get("contentType").toString());  // 1 表示pathway  2 表示newsletter
        String contentId = param.get("contentId").toString();
        Integer height = Integer.parseInt(param.get("height").toString());
        readOnSaveService.readOnSave(userId,contentType,contentId,height);
        return null;
    }

//    @GetMapping("/readOnList")
//    public
}
