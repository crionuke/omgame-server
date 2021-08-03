package com.crionuke.omgameserver.runtime.bootstrap;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class Bootstrap {
    static final Logger LOG = Logger.getLogger(Bootstrap.class);

    final String script;
    final Address address;

    Bootstrap(@ConfigProperty(name = "omgameserver.runtime.bootstrap.script") String script,
              @ConfigProperty(name = "omgameserver.runtime.bootstrap.address.tenant") String tenant,
              @ConfigProperty(name = "omgameserver.runtime.bootstrap.address.game") String game,
              @ConfigProperty(name = "omgameserver.runtime.bootstrap.address.worker") String worker) {
        this.script = script;
        address = new Address(tenant, game, worker);
        LOG.infof("Created, script='%s', address='%s'", script, address);
    }

    public Multi<Event> getMulti() {
        return Multi.createFrom().emitter(emitter -> {
            emitter.emit(new CreateWorkerEvent(script, address));
            emitter.complete();
        });
    }
}
