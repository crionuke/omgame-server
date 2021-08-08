package com.crionuke.omgameserver.core;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.List;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ConfigMapping(prefix = "omgameserver")
public interface Config {

    WebsocketConfig websocket();

    RuntimeConfig runtime();

    interface WebsocketConfig {
        @WithName("bufferSize")
        @WithDefault("128")
        int bufferSize();
    }

    interface RuntimeConfig {

        @WithName("bufferSize")
        @WithDefault("1024")
        int bufferSize();

        @WithName("jsonService")
        RuntimeJsonServiceConfig jsonService();

        @WithName("bootstrapService")
        RuntimeBootstrapService bootstrapService();
    }

    interface RuntimeJsonServiceConfig {
        @WithName("poolSize")
        @WithDefault("2")
        int poolSize();
    }

    interface RuntimeBootstrapService {
        @WithName("initialWorkers")
        List<RuntimeBootstrapServiceInitialWorkerConfig> initialWorkers();
    }

    interface RuntimeBootstrapServiceInitialWorkerConfig {
        @WithName("rootDirectory")
        @WithDefault(".")
        String rootDirectory();

        @WithName("mainScript")
        @WithDefault("main.lua")
        String mainScript();

        @WithName("tickEveryMillis")
        @WithDefault("1000")
        int tickEveryMillis();

        RuntimeBootstrapServiceInitialWorkerAddressConfig address();
    }

    interface RuntimeBootstrapServiceInitialWorkerAddressConfig {
        String tenant();

        String game();

        String worker();
    }
}