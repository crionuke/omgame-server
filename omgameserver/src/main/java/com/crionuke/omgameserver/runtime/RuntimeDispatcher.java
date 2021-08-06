package com.crionuke.omgameserver.runtime;

import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Dispatcher;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class RuntimeDispatcher extends Dispatcher {
    static final Logger LOG = Logger.getLogger(RuntimeDispatcher.class);

    public RuntimeDispatcher() {
        super(128, false);
    }

    public RuntimeDispatcher(Config config) {
        super(config.runtime().bufferSize(), false);
        LOG.infof("Created");
    }
}
