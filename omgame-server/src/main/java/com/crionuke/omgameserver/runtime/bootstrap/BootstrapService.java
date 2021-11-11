package com.crionuke.omgameserver.runtime.bootstrap;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import io.quarkus.runtime.Startup;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class BootstrapService {
    static final Logger LOG = Logger.getLogger(BootstrapService.class);

    final EventBus eventBus;
    final Config config;

    BootstrapService(EventBus eventBus, Config config) {
        this.eventBus = eventBus;
        this.config = config;
    }

    @PostConstruct
    void postConstruct() {
        for (Config.RuntimeBootstrapServiceInitialWorkerConfig initialWorkerConfig :
                config.runtime().bootstrapService().initialWorkers()) {
            String rootDirectory = initialWorkerConfig.rootDirectory();
            String mainScript = initialWorkerConfig.mainScript();
            Address address = Address.valueOf(initialWorkerConfig.address());
            int tickEveryMillis = initialWorkerConfig.tickEveryMillis();
            eventBus.publish(CreateWorkerEvent.TOPIC,
                    new CreateWorkerEvent(rootDirectory, mainScript, address, tickEveryMillis));
            LOG.infof("Worker bootstrapped, rootDirectory=%s, mainScript=%s, address=%s",
                    rootDirectory, mainScript, address);
        }
        LOG.infof("Started, initialWorkers=%s", config.runtime().bootstrapService().initialWorkers().stream()
                .map(worker -> Paths.get(worker.rootDirectory(), worker.mainScript()))
                .collect(Collectors.toList()));
    }
}
