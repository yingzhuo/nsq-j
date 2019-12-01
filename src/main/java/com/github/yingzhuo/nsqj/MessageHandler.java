package com.github.yingzhuo.nsqj;

public interface MessageHandler {

    void accept(Message msg);

}
