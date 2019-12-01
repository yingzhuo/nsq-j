package com.github.yingzhuo.nsqj;

public interface FailedMessageHandler {

    void failed(String topic, String channel, Message msg);

}
