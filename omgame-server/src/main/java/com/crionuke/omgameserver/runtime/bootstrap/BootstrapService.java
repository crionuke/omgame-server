package com.crionuke.omgameserver.runtime.bootstrap;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class BootstrapService extends AbstractVerticle {
    static final Logger LOG = Logger.getLogger(BootstrapService.class);

    final Config config;

    BootstrapService(Config config) {
        this.config = config;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        for (Config.RuntimeBootstrapServiceInitialWorkerConfig initialWorkerConfig :
                config.runtime().bootstrapService().initialWorkers()) {
            String rootDirectory = initialWorkerConfig.rootDirectory();
            String mainScript = initialWorkerConfig.mainScript();
            Address address = Address.valueOf(initialWorkerConfig.address());
            int tickEveryMillis = initialWorkerConfig.tickEveryMillis();
            vertx.eventBus().publish(CreateWorkerEvent.TOPIC,
                    new CreateWorkerEvent(rootDirectory, mainScript, address, tickEveryMillis));
            LOG.infof("Worker bootstrapped, rootDirectory=%s, mainScript=%s, address=%s",
                    rootDirectory, mainScript, address);
        }
        LOG.infof("Started, initialWorkers=%s", config.runtime().bootstrapService().initialWorkers().stream()
                .map(worker -> Paths.get(worker.rootDirectory(), worker.mainScript()))
                .collect(Collectors.toList()));
        startPromise.complete();
    }
}
