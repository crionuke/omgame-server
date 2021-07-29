package com.crionuke.omgameserver.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class Client {
    static final AtomicLong clientCounter = new AtomicLong();

    final long id;

    public Client() {
        id = clientCounter.incrementAndGet();
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ServerClient{" +
                "id=" + id +
                '}';
    }
}
