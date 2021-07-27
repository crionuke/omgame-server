package com.crionuke.omgameserver.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Service {

    final String serviceName;
    final Executor selfExecutor;

    public Service() {
        this.serviceName = Service.class.getSimpleName();
        selfExecutor = Executors.newSingleThreadExecutor(new ServiceThreadFactory());
    }

    public Service(String serviceName) {
        this.serviceName = serviceName;
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
            thread.setName(serviceName + "-" + threadCounter.incrementAndGet());
            return thread;
        }
    }
}
