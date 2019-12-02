package com.github.yingzhuo.nsqj;

import java.nio.charset.StandardCharsets;

public interface Message {

    String getTopic();

    byte[] getData();

    public default String getDataString() {
        return new String(getData(), StandardCharsets.UTF_8);
    }

    String getId();

    int getAttempts();

    long getTimestamp();

    void finish();

    void requeue();

    void touch();
}
