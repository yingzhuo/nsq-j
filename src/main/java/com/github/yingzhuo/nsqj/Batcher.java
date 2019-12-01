package com.github.yingzhuo.nsqj;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.yingzhuo.nsqj.Util.checkArgument;
import static com.github.yingzhuo.nsqj.Util.checkNotNull;

@Slf4j
class Batcher {

    private final Publisher publisher;
    private final String topic;
    private final int maxSize;
    private final int maxDelayMillis;
    private final ScheduledExecutorService executor;
    private int size;
    private List<byte[]> batch = new ArrayList<>();
    private long sendTime;

    public Batcher(Publisher publisher, String topic, int maxSizeBytes, int maxDelayMillis) {
        this.publisher = publisher;
        this.topic = topic;
        this.maxSize = maxSizeBytes;
        this.maxDelayMillis = maxDelayMillis;
        this.executor = publisher.getBatchExecutor();
        checkNotNull(publisher);
        checkNotNull(topic);
        checkArgument(maxDelayMillis > 5);
        checkArgument(maxDelayMillis <= 60000);
        checkArgument(maxSize > 100);
    }

    public void publish(byte[] msg) {
        boolean sendNow = false;
        synchronized (this) {
            batch.add(msg);
            size += msg.length;
            if (batch.size() == 1) {
                sendTime = Util.clock() + maxDelayMillis;
                executor.schedule(new Runnable() {
                    public void run() {
                        sendDelayedBatch();
                    }
                }, maxDelayMillis, TimeUnit.MILLISECONDS);
            } else if (size >= maxSize) {
                sendNow = true;
            }
        }
        if (sendNow) {
            sendBatch();
        }
    }

    private void sendDelayedBatch() {
        try {
            boolean sendNow = false;
            synchronized (this) {
                if (!batch.isEmpty()) {
                    long delay = sendTime - Util.clock();
                    if (delay < 50) {
                        sendNow = true;
                    } else {
                        executor.schedule(new Runnable() {
                            public void run() {
                                sendDelayedBatch();
                            }
                        }, delay, TimeUnit.MILLISECONDS);
                    }
                }
            }
            if (sendNow) {
                sendBatch();
            }
        } catch (Throwable t) {
            log.error("delayed batch error. messages possibly lost", t);
        }
    }

    void sendBatch() {
        List<byte[]> toSend = null;
        synchronized (this) {
            if (!batch.isEmpty()) {
                toSend = batch;
                int capacity = Math.min(maxSize, (int) (toSend.size() * 1.2));
                capacity = Math.max(10, capacity);
                batch = new ArrayList<byte[]>(capacity);
                size = 0;
            }
        }
        if (toSend != null) {
            publisher.publish(topic, toSend);
        }
    }

}
