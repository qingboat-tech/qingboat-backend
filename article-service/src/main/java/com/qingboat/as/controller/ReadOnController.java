package com.qingboat.as.controller;

import com.qingboat.as.service.ReadOnSaveService;
import com.qingboat.as.vo.ReadOnListVo;
import com.qingboat.as.vo.ReadOnVo;
import com.qingboat.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ReadOnController extends BaseController {

    @Autowired
    ReadOnSaveService readOnSaveService;
    //这里可以使用redis缓存
    @PostMapping("/readOnSave")
    public ApiResponse readOnSave(@RequestBody Map<String,Object> param){
        Integer userId = getUId().intValue();
        Integer contentType = Integer.parseInt(param.get("contentType").toString());  // 1 表示pathway  2 表示newsletter
        String contentId = param.get("contentId").toString();
        Integer height = Integer.parseInt(param.get("height").toString());
        Integer pathwayId = 0;
        if (param.get("pathwayId") != null){
            pathwayId =  Integer.parseInt(param.get("pathwayId").toString());
        }
        return readOnSaveService.readOnSave(userId,contentType,contentId,height,pathwayId);
    }

    @GetMapping("/readOnList")
    public ReadOnListVo readOnList(@RequestParam("page")Integer page,@RequestParam("pageSize")Integer pageSize){
//        ReadOnListVo readOnListVo = new ReadOnListVo();
//        readOnListVo.setTotal(1);
//        List<ReadOnVo> list = new ArrayList<>();
//        ReadOnVo readOnVo = new ReadOnVo();
//        readOnVo.setCreatorId(1);
//        readOnVo.setContentType(1);
//        readOnVo.setHeight(10);
//        readOnVo.setDesc("1十大");
//        readOnVo.setDesc("desccc");
//        readOnVo.setNickname("nickname");
//        readOnVo.setCreatorimgUrl("url");
//        readOnVo.setHeadimgUrl("headurl");
//        readOnVo.setPathwayName("pathwayname");
//        readOnVo.setTitle("titles");
//        list.add(readOnVo);
//        readOnListVo.setList(list);
//        return readOnListVo;
        Integer userId = getUId().intValue();
        return readOnSaveService.readOnList(userId,page,pageSize);
    }
}
