package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Event;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class TickEvent extends Event {

    public static final String TOPIC = "TickEvent";

    long tick;
    long time;

    public TickEvent(long tick, long time) {
        this.tick = tick;
        this.time = time;
    }

    public long getTick() {
        return tick;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TickEvent{" +
                "tick=" + tick +
                ", time=" + time +
                '}';
    }
}
