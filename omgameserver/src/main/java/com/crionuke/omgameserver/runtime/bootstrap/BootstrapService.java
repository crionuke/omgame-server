package com.crionuke.omgameserver.runtime.bootstrap;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class BootstrapService {
    static final Logger LOG = Logger.getLogger(BootstrapService.class);

    final Config config;

    BootstrapService(Config config) {
        this.config = config;
        LOG.infof("Created");
    }

    public Multi<Event> getMulti() {
        return Multi.createFrom().emitter(emitter -> {
            for (Config.RuntimeBootstrapServiceInitialWorkerConfig initialWorkerConfig :
                    config.runtime().bootstrapService().initialWorkers()) {
                String rootDirectory = initialWorkerConfig.rootDirectory();
                String mainScript = initialWorkerConfig.mainScript();
                Address address = Address.valueOf(initialWorkerConfig.address());
                int tickEveryMillis = initialWorkerConfig.tickEveryMillis();
                emitter.emit(new CreateWorkerEvent(rootDirectory, mainScript, address, tickEveryMillis));
                LOG.infof("Worker bootstrapped, rootDirectory=%s, mainScript=%s, address=%s",
                        rootDirectory, mainScript, address);
            }
            emitter.complete();
        });
    }
}
