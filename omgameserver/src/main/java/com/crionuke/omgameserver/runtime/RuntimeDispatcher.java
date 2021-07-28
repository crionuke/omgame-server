package com.crionuke.omgameserver.runtime;

import com.crionuke.omgameserver.core.Dispatcher;
import com.crionuke.omgameserver.runtime.events.RuntimeEvent;
import com.crionuke.omgameserver.runtime.events.TickEvent;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class RuntimeDispatcher extends Dispatcher<RuntimeEvent> {
    static final Logger LOG = Logger.getLogger(RuntimeDispatcher.class);

    final int tickEveryMillis;

    public RuntimeDispatcher(
            @ConfigProperty(name = "omgameserver.runtime.bufferSize", defaultValue = "1024") int bufferSize,
            @ConfigProperty(name = "omgameserver.runtime.tickEveryMillis", defaultValue = "100") int tickEveryMillis) {
        super(bufferSize, false);
        this.tickEveryMillis = tickEveryMillis;
        LOG.infof("Created, bufferSize=%d, tickEveryMillis=%d", bufferSize, tickEveryMillis);
    }

    @Override
    public Multi<RuntimeEvent> getMulti() {
        // Mix with ticks
        return Multi.createBy().merging().streams(getTicks(), super.getMulti());
    }

    Multi<RuntimeEvent> getTicks() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(tickEveryMillis))
                .onItem().transform(tick -> new TickEvent(tick));
    }
}
