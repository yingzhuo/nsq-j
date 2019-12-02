/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj;

import java.util.HashSet;
import java.util.Set;

/**
 * Subscribe from a given set of nsqd hosts instead of using the lookup service.
 *
 * @author 应卓
 * @since 1.0.0
 */
public class DirectSubscriber extends Subscriber {

    private final Set<HostAndPort> nsqds = new HashSet<>();

    public DirectSubscriber(int checkHostsIntervalSecs, String... nsqdHosts) {
        super(checkHostsIntervalSecs);
        for (String h : nsqdHosts) {
            nsqds.add(HostAndPort.fromString(h).withDefaultPort(4150));
        }
    }

    @Override
    protected Set<HostAndPort> lookupTopic(String topic) {
        return nsqds;
    }

}
