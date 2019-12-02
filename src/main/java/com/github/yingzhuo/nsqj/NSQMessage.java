/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj;

class NSQMessage implements Message {

    private final long timestamp;
    private final int attempts;
    private final String id;
    private final byte[] data;
    private final String topic;
    private final SubConnection connection;

    NSQMessage(long timestamp, int attempts, String id, byte[] data, String topic, SubConnection connection) {
        this.timestamp = timestamp;
        this.attempts = attempts;
        this.id = id;
        this.data = data;
        this.topic = topic;
        this.connection = connection;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int getAttempts() {
        return attempts;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public void finish() {
        connection.finish(id);
    }

    @Override
    public void requeue() {
        connection.requeue(id);
    }

    @Override
    public void touch() {
        connection.touch(id);
    }

}
