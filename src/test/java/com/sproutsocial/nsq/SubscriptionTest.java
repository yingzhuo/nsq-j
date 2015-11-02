package com.sproutsocial.nsq;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.methods;
import static org.powermock.api.mockito.PowerMockito.suppress;

//@PrepareForTest({Subscription.class, SubConnection.class})

@RunWith(PowerMockRunner.class)
@PrepareForTest(SubConnection.class)
public class SubscriptionTest {

    @Before
    public void beforeClass() {
        suppress(methods(SubConnection.class, "connect"));
        suppress(methods(SubConnection.class, "sendReady"));
    }

    @Test
    public void testDistributeInFlight() throws Exception {
        Subscriber subscriber = new Subscriber();
        subscriber.setMaxInFlightPerSubscription(200);
        Subscription sub = new Subscription("topic", "channel", null, subscriber);

        testInFlight(1, 1, sub, 200);
        testInFlight(2, 2, sub, 100, 100);
        testInFlight(2, 1, sub, 199);
        testInFlight(3, 3, sub, 66, 67, 67);
        testInFlight(5, 3, sub, 66, 66, 66);

        subscriber.setMaxInFlightPerSubscription(2500);
        testInFlight(4, 4, sub, 625, 625, 625, 625);
        testInFlight(6, 6, sub, 417, 417, 417, 417, 416, 416);
        testInFlight(7, 4, sub, 624, 624, 624, 625);
        testInFlight(10, 6, sub, 416, 416, 416, 416, 416, 416);
    }

    private void testInFlight(int numCons, int numActive, Subscription sub, Integer... activeInFlight) {
        Map<HostAndPort, SubConnection> conMap = Whitebox.getInternalState(sub, "connectionMap");
        for (SubConnection con : conMap.values()) {
            con.lastActionFlush = 5000; //so connections get removed during checkConnections
        }
        checkHosts(sub, numCons);
        Set<SubConnection> allCons = new HashSet<SubConnection>(conMap.values());
        Set<SubConnection> active = setActive(numActive, allCons);
        Set<SubConnection> inactive = Sets.difference(allCons, active);
        checkHosts(sub, numCons);
        assertFalse(sub.isLowFlight());
        assertEquals(numCons, conMap.size());
        assertEquals(Sets.union(active, inactive), new HashSet<SubConnection>(conMap.values()));

        for (SubConnection con : inactive) {
            assertEquals(1, con.getMaxInFlight());
        }
        Multiset<Integer> expectedMaxInFlight = HashMultiset.create(Arrays.asList(activeInFlight));
        Multiset<Integer> actualMaxInFlight = HashMultiset.create();
        for (SubConnection con : active) {
            actualMaxInFlight.add(con.getMaxInFlight());
        }
        assertEquals(expectedMaxInFlight, actualMaxInFlight);
    }

    private void checkHosts(Subscription sub, int count) {
        Set<HostAndPort> hosts = new HashSet<HostAndPort>();
        for (int i = 0; i < count; i++) {
            hosts.add(HostAndPort.fromParts("host" + i, 123));
        }
        sub.checkConnections(hosts);
    }

    private Set<SubConnection> setActive(int numActive, Collection<SubConnection> cons) {
        Set<SubConnection> activeSet = new HashSet<SubConnection>();
        for (SubConnection con : cons) {
            if (activeSet.size() < numActive) {
                con.lastActionFlush = Client.clock() - 1000;
                activeSet.add(con);
            }
            else {
                con.lastActionFlush = 5000;
            }
        }
        return activeSet;
    }

    @Test
    public void testLowFlight() throws Exception {
        Subscriber subscriber = new Subscriber();
        Subscription sub = new Subscription("topic", "channel", null, subscriber);

        testLowFlight(subscriber, sub, 2, 3);
        testLowFlight(subscriber, sub, 1, 2);
        testLowFlight(subscriber, sub, 5, 10);
    }

    private Set<SubConnection> getReady(Collection<SubConnection> cons) {
        Set<SubConnection> ready = new HashSet<SubConnection>();
        for (SubConnection con : cons) {
            //System.out.println(con.getHost() + " maxInFlight:" + con.getMaxInFlight());
            if (con.getMaxInFlight() != 0) {
                ready.add(con);
            }
        }
        return ready;
    }

    private void testLowFlight(Subscriber subscriber, Subscription sub, int maxInflight, int numCons) throws Exception {
        subscriber.setMaxInFlightPerSubscription(maxInflight);
        checkHosts(sub, numCons);
        assertTrue(sub.isLowFlight());
        Map<HostAndPort, SubConnection> conMap = Whitebox.getInternalState(sub, "connectionMap");

        Set<SubConnection> ready = getReady(conMap.values());
        Set<SubConnection> paused = Sets.difference(new HashSet<SubConnection>(conMap.values()), ready);

        assertEquals(maxInflight, ready.size());
        assertEquals(numCons - maxInflight, paused.size());

        Whitebox.invokeMethod(sub, "rotateLowFlight");

        Set<SubConnection> nextReady = getReady(conMap.values());
        Set<SubConnection> nextPaused = Sets.difference(new HashSet<SubConnection>(conMap.values()), nextReady);

        assertEquals(maxInflight, nextReady.size());
        assertEquals(numCons - maxInflight, nextPaused.size());
        assertEquals(1, Sets.difference(ready, nextReady).size());
        assertEquals(1, Sets.difference(paused, nextPaused).size());
    }

}