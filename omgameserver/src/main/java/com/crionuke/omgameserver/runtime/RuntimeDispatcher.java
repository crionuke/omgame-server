package com.crionuke.omgameserver.runtime;

import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Dispatcher;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.runtime.events.TickEvent;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class RuntimeDispatcher extends Dispatcher {
    static final Logger LOG = Logger.getLogger(RuntimeDispatcher.class);

    final int tickEveryMillis;

    public RuntimeDispatcher() {
        super(128, false);
        tickEveryMillis = 100;
    }

    public RuntimeDispatcher(Config config) {
        super(config.runtime().bufferSize(), false);
        this.tickEveryMillis = config.runtime().tickEveryMillis();
        LOG.infof("Created");
    }

    @Override
    public Multi<Event> getMulti() {
        // Mix with ticks
        return Multi.createBy().merging().streams(getTicks(), super.getMulti());
    }

    Multi<Event> getTicks() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(tickEveryMillis))
                .onItem().transform(tick -> new TickEvent(tick));
    }
}
