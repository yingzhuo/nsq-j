/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

abstract class BasePubSub {

    protected Config config = new Config();
    protected volatile boolean isStopping = false;
    protected final Client client;
    private final List<ScheduledFuture> tasks = Collections.synchronizedList(new ArrayList<>());

    protected BasePubSub(Client client) {
        this.client = client;
    }

    public final Client getClient() {
        return client;
    }

    public synchronized Config getConfig() {
        return config;
    }

    public synchronized void setConfig(Config config) {
        this.config = config;
    }

    protected void scheduleAtFixedRate(Runnable runnable, int initialDelay, int period, boolean jitter) {
        if (!isStopping) {
            tasks.add(client.scheduleAtFixedRate(runnable, initialDelay, period, jitter));
        }
    }

    public void stop() {
        isStopping = true;
        cancelTasks();
    }

    protected void cancelTasks() {
        synchronized (tasks) {
            for (ScheduledFuture task : tasks) {
                task.cancel(false);
            }
            tasks.clear();
        }
    }

}
