package com.github.yingzhuo.nsqj;

public interface MessageDataHandler {

    void accept(byte[] data);

}
