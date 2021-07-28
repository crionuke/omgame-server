package com.crionuke.omgameserver.core;

import java.util.concurrent.atomic.AtomicLong;

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
