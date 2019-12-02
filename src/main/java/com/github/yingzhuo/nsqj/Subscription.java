/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static com.github.yingzhuo.nsqj.Util.copy;

@Slf4j
class Subscription extends BasePubSub {

    private final String topic;
    private final String channel;
    private final MessageHandler handler;
    private final Subscriber subscriber;
    private final Map<HostAndPort, SubConnection> connectionMap = Collections.synchronizedMap(new HashMap<>());
    private int maxInFlight;
    private ScheduledFuture lowFlightRotateTask;

    public Subscription(Client client, String topic, String channel, MessageHandler handler, Subscriber subscriber, int maxInFlight) {
        super(client);
        this.topic = topic;
        this.channel = channel;
        this.handler = handler;
        this.subscriber = subscriber;
        this.maxInFlight = maxInFlight;
    }

    public synchronized int getMaxInFlight() {
        return maxInFlight;
    }

    public synchronized void setMaxInFlight(int maxInFlight) {
        this.maxInFlight = maxInFlight;
        distributeMaxInFlight();
    }

    public synchronized void checkConnections(Set<HostAndPort> activeHosts) {
        synchronized (connectionMap) {
            for (Iterator<SubConnection> iterator = connectionMap.values().iterator(); iterator.hasNext(); ) {
                SubConnection con = iterator.next();
                if (!activeHosts.contains(con.getHost())) {
                    if (Util.clock() - con.getLastActionFlush() > con.getMsgTimeout() * 100) {
                        log.info("closing inactive connection:{} topic:{}", con.getHost(), topic);
                        iterator.remove();
                        con.close();
                    }
                }
            }
        }
        for (HostAndPort activeHost : activeHosts) {
            if (!connectionMap.containsKey(activeHost)) {
                try {
                    log.info("adding new connection:{} topic:{}", activeHost, topic);
                    SubConnection con = new SubConnection(client, activeHost, this);
                    con.connect(subscriber.getConfig());
                    connectionMap.put(activeHost, con);
                } catch (Exception e) {
                    log.error("error connecting to:{}", activeHost, e);
                }
            }
        }
        distributeMaxInFlight();
    }

    private void distributeMaxInFlight() {
        if (checkLowFlight() || connectionMap.isEmpty()) {
            return;
        }
        List<SubConnection> activeCons = new ArrayList<>();
        List<SubConnection> inactiveCons = new ArrayList<>();
        long minActiveTime = Util.clock() - (subscriber.getLookupIntervalSecs() + 40) * 1000;
        for (SubConnection con : copy(connectionMap.values())) {
            if (con.lastActionFlush < minActiveTime) {
                inactiveCons.add(con);
            } else {
                activeCons.add(con);
            }
        }
        if (activeCons.isEmpty()) {
            activeCons.addAll(inactiveCons);
            inactiveCons.clear();
        }
        for (SubConnection con : inactiveCons) {
            con.setMaxInFlight(1, false);
        }
        int f = maxInFlight - inactiveCons.size();
        int perCon = f / activeCons.size();
        int extra = f % activeCons.size();
        for (SubConnection con : activeCons) {
            int c = perCon;
            if (extra > 0) {
                c++;
                extra--;
            }
            con.setMaxInFlight(Math.min(c, con.getMaxRdyCount()));
        }
    }

    private boolean checkLowFlight() {
        if (maxInFlight < connectionMap.size()) {
            if (lowFlightRotateTask == null) {
                lowFlightRotateTask = client.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        rotateLowFlight();
                    }
                }, 10000, 10000, false);
            }
            List<SubConnection> cons = copy(connectionMap.values());
            for (SubConnection con : cons.subList(0, maxInFlight)) {
                con.setMaxInFlight(1);
            }
            for (SubConnection con : cons.subList(maxInFlight, cons.size())) {
                con.setMaxInFlight(0);
            }
            return true;
        }
        Util.cancel(lowFlightRotateTask);
        lowFlightRotateTask = null;
        return false;
    }

    private synchronized void rotateLowFlight() {
        SubConnection paused = null;
        SubConnection ready = null;
        for (SubConnection con : copy(connectionMap.values())) {
            if (con.getMaxInFlight() == 0 && (paused == null || con.getLastActionFlush() < paused.getLastActionFlush())) {
                paused = con;
            } else if (con.getMaxInFlight() == 1 && (ready == null || con.getLastActionFlush() < ready.getLastActionFlush())) {
                ready = con;
            }
        }
        if (ready != null && paused != null) { //should always be true
            ready.setMaxInFlight(0);
            paused.setMaxInFlight(1);
        }
    }

    @Override
    public void stop() {
        super.stop();
        synchronized (this) {
            Util.cancel(lowFlightRotateTask);
            lowFlightRotateTask = null;
        }
        for (SubConnection con : copy(connectionMap.values())) {
            con.stop();
        }
    }

    public synchronized void connectionClosed(SubConnection closedCon) {
        if (connectionMap.get(closedCon.getHost()) == closedCon) {
            connectionMap.remove(closedCon.getHost());
            log.debug("removed:{} from subscription:{}", closedCon.getHost(), topic);
        }
    }

    public synchronized boolean isLowFlight() {
        return lowFlightRotateTask != null;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public MessageHandler getHandler() {
        return handler;
    }

    public String getTopic() {
        return topic;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return String.format("subscription %s.%s connections:%s", topic, channel, connectionMap.size());
    }

    public int getConnectionCount() {
        return connectionMap.size();
    }

}
