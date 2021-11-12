package com.crionuke.omgameserver.core;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class Factory implements ThreadFactory {

    final String prefix;
    final AtomicLong counter;

    public Factory(String prefix) {
        this.prefix = prefix;
        counter = new AtomicLong();
    }

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("omg.s-" + prefix + "-" + "thread-" + counter.incrementAndGet());
        return thread;
    }
}
