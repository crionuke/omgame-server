package com.crionuke.omgameserver.core;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public abstract class Dispatcher<T> {

    final BroadcastProcessor<T> processor;
    final Multi<T> multi;

    public Dispatcher() {
        processor = BroadcastProcessor.create();
        multi = processor;
    }

    public Dispatcher(int bufferSize, boolean drop) {
        processor = BroadcastProcessor.create();
        if (bufferSize > 0) {
            if (drop) {
                multi = processor.onOverflow().buffer(bufferSize).onOverflow().drop();
            } else {
                multi = processor.onOverflow().buffer(bufferSize);
            }
        } else {
            throw new IllegalArgumentException("bufferSize need to greater than zero");
        }
    }

    public Multi<T> getMulti() {
        return multi;
    }

    public void fire(T event) {
        processor.onNext(event);
    }
}