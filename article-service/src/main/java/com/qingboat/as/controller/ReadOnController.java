package com.qingboat.as.controller;

import com.qingboat.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ReadOnController {

    @PostMapping("/readOnSave")
    public ApiResponse readOnSave(){
        return null;
    }
}
