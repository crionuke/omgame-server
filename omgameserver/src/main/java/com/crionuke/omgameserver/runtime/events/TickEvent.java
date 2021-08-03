package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Event;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class TickEvent extends Event {

    long tick;

    public TickEvent(long tick) {
        this.tick = tick;
    }

    public long getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return "TickEvent{" +
                "tick=" + tick +
                '}';
    }
}
