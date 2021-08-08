package com.crionuke.omgameserver.runtime.bootstrap;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class Bootstrap {
    static final Logger LOG = Logger.getLogger(Bootstrap.class);

    final Config config;

    Bootstrap(Config config) {
        this.config = config;
        LOG.infof("Created");
    }

    public Multi<Event> getMulti() {
        return Multi.createFrom().emitter(emitter -> {
            for (Config.RuntimeBootstrap bootstrap : config.runtime().bootstrap()) {
                String rootDirectory = bootstrap.rootDirectory();
                String mainScript = bootstrap.mainScript();
                Address address = Address.valueOf(bootstrap.address());
                int tickEveryMillis = bootstrap.tickEveryMillis();
                emitter.emit(new CreateWorkerEvent(rootDirectory, mainScript, address, tickEveryMillis));
                LOG.infof("Worker bootstrapped, rootDirectory=%s, mainScript=%s, address=%s",
                        rootDirectory, mainScript, address);
            }
            emitter.complete();
        });
    }
}
