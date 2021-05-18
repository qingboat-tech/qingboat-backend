package com.qingboat.us.task;

import com.qingboat.base.task.DelayTask;
import com.qingboat.us.service.UserService;

import java.util.UUID;

public class ApplyCreatorTask extends DelayTask {

    private UserService userService;
    private Long userId;

    public ApplyCreatorTask(UserService userService,Long userId, long expire) {
        super(UUID.randomUUID().toString(), expire);
        this.userService = userService;
        this.userId = userId;
    }

    @Override
    public void processTask() {
        userService.applyCreator(userId);
    }

    public static void main(String[] arg){

        ApplyCreatorTask task = new ApplyCreatorTask(null,12L,1000*60);

        System.err.println(task);
    }
}
