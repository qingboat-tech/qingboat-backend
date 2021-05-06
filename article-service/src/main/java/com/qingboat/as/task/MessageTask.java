package com.qingboat.as.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageTask {

    private int i;


    @Scheduled(cron = "*/15 * * * * ?")
    public void execute() {
        log.info("thread id:{},FixedPrintTask execute times:{}", Thread.currentThread().getId(), ++i);
    }

}
