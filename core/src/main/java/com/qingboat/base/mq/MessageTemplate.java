package com.qingboat.base.mq;


public class MessageTemplate<T> {

    private String messageId;
    private Integer messageType;
    private T t;

    public MessageTemplate() {
    }

    public MessageTemplate(String messageId,Integer messageType,T t) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.t = t;
    }



}
