package com.crionuke.omgameserver.runtime;

import com.crionuke.omgameserver.core.Dispatcher;
import com.crionuke.omgameserver.runtime.events.RuntimeEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class RuntimeDispatcher extends Dispatcher<RuntimeEvent> {
    static final Logger LOG = Logger.getLogger(RuntimeDispatcher.class);

    public RuntimeDispatcher(
            @ConfigProperty(name = "omgameserver.runtime.bufferSize", defaultValue = "1024") int bufferSize) {
        super(bufferSize, false);
        LOG.infof("Created, bufferSize=%d", bufferSize);
    }
}
