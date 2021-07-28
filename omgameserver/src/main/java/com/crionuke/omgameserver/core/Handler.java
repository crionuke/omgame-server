package com.crionuke.omgameserver.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public abstract class Handler {

    final String handlerName;
    final Executor selfExecutor;

    public Handler() {
        this.handlerName = Handler.class.getSimpleName();
        selfExecutor = Executors.newSingleThreadExecutor(new ServiceThreadFactory());
    }

    public Handler(String handlerName) {
        this.handlerName = handlerName;
        selfExecutor = Executors.newSingleThreadExecutor(new ServiceThreadFactory());
    }

    public Executor getSelfExecutor() {
        return selfExecutor;
    }

    class ServiceThreadFactory implements ThreadFactory {
        static private final AtomicLong threadCounter = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(handlerName + "-" + threadCounter.incrementAndGet());
            return thread;
        }
    }
}
