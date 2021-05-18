package com.qingboat.base.task;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public  class DelayTask implements Delayed {

    final private String taskId;
    final private long expire;

    public DelayTask(String taskId,long expire){
        super();
        this.taskId = taskId;
        this.expire = expire + System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), unit);
    }

    @Override
    public int compareTo(Delayed o) {
        long delta = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (int) delta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DelayTask) {
            return this.taskId.equals(((DelayTask) obj).taskId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + "taskId:" + taskId + "," + "expire:" + new Date(expire) + "}";
    }


    public  void processTask(){

    }

}
