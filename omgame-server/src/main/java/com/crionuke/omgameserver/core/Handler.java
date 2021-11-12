package com.crionuke.omgameserver.core;

import io.smallrye.mutiny.Uni;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class Handler {

    protected final Executor selfExecutor;

    public Handler() {
        this("pool", 1);
    }

    public Handler(String prefix) {
        this(prefix, 1);
    }

    public Handler(String prefix, int nThreads) {
        selfExecutor = Executors.newFixedThreadPool(nThreads, new Factory(prefix));
    }

    protected Uni<Void> invoke(Runnable runnable) {
        return Uni.createFrom().voidItem().emitOn(selfExecutor)
                .onItem().invoke(runnable);
    }

    protected Uni<Void> call(Supplier<Uni<?>> supplier) {
        return Uni.createFrom().voidItem().emitOn(selfExecutor)
                .onItem().call(supplier);
    }
}
